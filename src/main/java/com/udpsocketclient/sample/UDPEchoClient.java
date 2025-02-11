package com.udpsocketclient.sample;

import com.google.gson.Gson;
import com.udpsocketclient.sample.config.MqConfig;
import com.udpsocketclient.sample.dto.LocationDto;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@Slf4j
public class UDPEchoClient {

    private String str;
    private BufferedReader file;
    private static int SERVER_PORT = 9991;
    private static int CLIENT_PORT = 2000;

    public static String EXCHANGE_LOCATION = "location";
    public static String ROUTING_KEY_LOCATION = "location.person.*";
    public static String QUEUE_NAME_PERSON = "person";

    public static String ROUTING_KEY_FORKLIFT = "location.forklift.*";
    public static String QUEUE_NAME_FORKLIFT = "forklift";

    private static ExecutorService executorService = Executors.newFixedThreadPool(4); // 4개의 스레드 생성
    private static final Map<Long, String> recentMessages = new HashMap<>(); // 중복 방지 또는 최신 데이터 처리



    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {

        DatagramSocket ds = new DatagramSocket(SERVER_PORT); // 패킷을 전송받을 포트를 열어둔다.
        ds.setSoTimeout(2000); // 5초 타임아웃 설정
        ds.setReceiveBufferSize(4096);


        try {

            System.out.println("== UDP Client Start==");
            while (true) { // 무한루프 통해 메시지 계속 전송받는다
                try {
                    byte[] data = new byte[1024]; // 한번에 받을 수 있는 최대 용량의 데이터 공간은 기본 정보 공간을 제외한 65,508 byte이다
                    //따라서 이 공간만큼을 미리 확보해 둔다.
                    DatagramPacket dp = new DatagramPacket(data, data.length);//데이터를 전송받을 객체를 생성한다.
                    ds.receive(dp); //패킷을 받는다
//                log.info(dp.getAddress().getHostAddress() +
//                        " >> " + new String(dp.getData()).trim());  //전송받은 패킷이 발송된 곳의 IP주소와 내용 출력

                    executorService.submit(() -> processPacket(dp)); // 비동기로 패킷 처리

                } catch (SocketTimeoutException e) {
                    // 타임아웃 예외 발생 시 처리
                    log.error("Timeout: No response from server. Retrying...");
                    // 타임아웃 발생 시에도 루프는 계속 진행
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }

    /**
     * 수신한 UDP 패킷 처리
     */
    private static void processPacket(DatagramPacket dp) {
        try {
            String jsonStr = new String(dp.getData(), 0, dp.getLength(), StandardCharsets.UTF_8).trim();
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(jsonStr, HashMap.class);

            if (map == null || map.isEmpty()) {
                log.warn("Received empty or invalid packet: {}", jsonStr);
                return;
            }

            LocationDto locationDto = parseLocationData(map); // 수신 데이터 파싱
            if (locationDto == null) return;

            // RabbitMQ 데이터 전송
            sendMqMessage(locationDto);
        } catch (Exception e) {
            log.error("Failed to process packet", e);
        }
    }


    /**
     * JSON 데이터를 기반으로 LocationDto 생성
     */
    private static LocationDto parseLocationData(Map<String, Object> map) {
        LocationDto locationDto = new LocationDto();

        try {
            for(String key : map.keySet()) {
                // JSON 데이터 파싱
                if (key.equals("id")) {
                    int idx = (int) Math.round((double) map.get("id"));
                    locationDto.setId(idx);
                }
                if (key.equals("name")) locationDto.setName(map.get(key).toString());


                if (key.equals("spaceId")) {
                    long spaceId = Math.round((double) map.get("spaceId"));
                    locationDto.setSpaceId(spaceId);
                }
                // -- 보정값 더하기 + 일 경우 왼쪽, - 일 경우 오른쪽
                if (locationDto.getSpaceId() == 1L) {
                    if (key.equals("x")) {

                        // offsetdl + 일 경우
                        double offset = 0.0;
                        double x = ((double) map.get(key)) - 2010.625 + offset;
                        // - 28
                        // 28


//                                if(x >= 50) x = 50;
//                                else if(x < -50) x = -50;

                        locationDto.setX(x);
                    }
                    if (key.equals("y")) {
                        double offset = 0.0;
                        double y = ((double) map.get(key)) - 1051.968 + offset;

//                                if( y>= 20.1) y = 20.1;
//                                else if (y <= -20.1) y = -20.1;

                        locationDto.setY(y);
                    }
                    if (key.equals("z")) locationDto.setZ((double) map.get(key));
                } else if (locationDto.getSpaceId() == 2L) {
                    if (key.equals("x")) {

                        double x = ((double) map.get(key)) - 1999.178;

//                                if(x >= 28.5) x = 28.5;
//                                else if(x < -28.5) x = -28.5;

                        locationDto.setX(x);
                    }
                    if (key.equals("y")) {

                        double y = ((double) map.get(key)) - 1095.567;

                        locationDto.setY(y);

                    }
                    if (key.equals("z")) locationDto.setZ((double) map.get(key));
                }
                if (key.equals("deviceId")) {
                    locationDto.setDeviceId(map.get(key).toString());
                }
                if (key.equals("count")) {
                    long idx = Math.round((double) map.get("count"));
                    locationDto.setCount(idx);
                }
                if (key.equals("timestamp")) {
                    long timestamp = Math.round((double) map.get("timestamp"));
                    locationDto.setTimestamp(timestamp);
                }

            }
            calculateCoordinates(locationDto, map);

            return locationDto;
        } catch (Exception e) {
            log.error("Error parsing location data: {}", map, e);
            return null; // 데이터 파싱 실패 시 null 반환
        }
    }

    /**
     * 좌표 계산 (spaceId에 따라 보정)
     */
    private static void calculateCoordinates(LocationDto locationDto, Map<String, Object> map) {
        double offsetX = 0.0, offsetY = 0.0;

        if (locationDto.getSpaceId() == 1L) {
            locationDto.setX(((double) map.getOrDefault("x", 0.0)) - 2010.625 + offsetX);
            locationDto.setY(((double) map.getOrDefault("y", 0.0)) - 1051.968 + offsetY);
        } else if (locationDto.getSpaceId() == 2L) {
            locationDto.setX(((double) map.getOrDefault("x", 0.0)) - 1999.178);
            locationDto.setY(((double) map.getOrDefault("y", 0.0)) - 1095.567);
        }
        locationDto.setZ((double) map.getOrDefault("z", 0.0));
    }

    /**
     * RabbitMQ로 메시지 전송
     */
    private static void sendMqMessage(LocationDto locationDto) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(locationDto, LocationDto.class);

        MqConfig mqConfig = isForklift(locationDto) ?
                new MqConfig(EXCHANGE_LOCATION, ROUTING_KEY_FORKLIFT, QUEUE_NAME_FORKLIFT) :
                new MqConfig(EXCHANGE_LOCATION, ROUTING_KEY_LOCATION, QUEUE_NAME_PERSON);

        try {
            log.info((isForklift(locationDto) ? "fork" : "person") + " : " + jsonStr);
            mqConfig.Send(jsonStr); // 메시지 전송
        } catch (IOException | TimeoutException | InterruptedException e) {
            log.error("Failed to send message to RabbitMQ", e);
        }
    }

    /**
     * 포크리프트 판단 로직
     */
    private static boolean isForklift(LocationDto locationDto) {
        return locationDto.getId() == 8 || locationDto.getId() == 9 ||
                locationDto.getId() == 11 || locationDto.getId() == 25 ||
                locationDto.getId() == 28 || locationDto.getId() == 29 ||
                locationDto.getId() == 104 || locationDto.getId() == 108;
    }

}
