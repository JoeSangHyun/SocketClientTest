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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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

    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {

        MqConfig mqConfigPerson = new MqConfig(EXCHANGE_LOCATION,ROUTING_KEY_LOCATION,QUEUE_NAME_PERSON);
        MqConfig mqConfigFork = new MqConfig(EXCHANGE_LOCATION,ROUTING_KEY_FORKLIFT,QUEUE_NAME_FORKLIFT);
        DatagramSocket ds = new DatagramSocket(SERVER_PORT); // 패킷을 전송받을 포트를 열어둔다.
        LocationDto locationDto = new LocationDto();

        try {
            System.out.println("== UDP Client Start==");
            while (true) { // 무한루프 통해 메시지 계속 전송받는다
                byte[] data = new byte[65508]; // 한번에 받을 수 있는 최대 용량의 데이터 공간은 기본 정보 공간을 제외한 65,508 byte이다
                //따라서 이 공간만큼을 미리 확보해 둔다.
                DatagramPacket dp = new DatagramPacket(data, data.length);//데이터를 전송받을 객체를 생성한다.
                ds.receive(dp); //패킷을 받는다
//                log.info(dp.getAddress().getHostAddress() +
//                        " >> " + new String(dp.getData()).trim());  //전송받은 패킷이 발송된 곳의 IP주소와 내용 출력
                Gson gson = new Gson();

                String str = new String(dp.getData()).trim();
                Map<String, Object> map = gson.fromJson(str, HashMap.class);

                if(map != null) {

                    for(String key : map.keySet()) {
//                        long spaceId = 0;
                        if(key.equals("id")) {
                            int idx = (int)Math.round((double)map.get("id"));
                            locationDto.setId(idx);
                        }
                        if(key.equals("name")) locationDto.setName(map.get(key).toString());


                        if(key.equals("spaceId")) {
                            long spaceId = Math.round((double)map.get("spaceId"));
                            locationDto.setSpaceId(spaceId);
                        }
                        // -- 보정값 더하기 + 일 경우 왼쪽, - 일 경우 오른쪽
                        if( locationDto.getSpaceId() == 1L) {
                            if(key.equals("x")) {

                                // offsetdl + 일 경우
                                double offset = -10.0;
                                double x = ((double)map.get(key)) - 2010.625 + offset;


//                                if(x >= 50) x = 50;
//                                else if(x < -50) x = -50;

                                locationDto.setX(x);
                            }
                            if(key.equals("y")) {
                                double offset = 0.0;
                                double y = ((double)map.get(key)) - 1051.968 + offset;

//                                if( y>= 20.1) y = 20.1;
//                                else if (y <= -20.1) y = -20.1;

                                locationDto.setY(y);
                            }
                            if(key.equals("z")) locationDto.setZ((double)map.get(key));
                        } else if (locationDto.getSpaceId() == 2L) {
                            if(key.equals("x")) {

                                double x = ((double)map.get(key)) - 1999.178;

//                                if(x >= 28.5) x = 28.5;
//                                else if(x < -28.5) x = -28.5;

                                locationDto.setX(x);
                            }
                            if(key.equals("y")) {

                                double y = ((double)map.get(key)) - 1095.567;

//                                if( y>= 8) y = 8;
//                                else if (y <= -8) y = -8;

                                locationDto.setY(y);

                            }
                            if(key.equals("z")) locationDto.setZ((double)map.get(key));
                        }
                        if(key.equals("deviceId")) {
                            locationDto.setDeviceId(map.get(key).toString());
                        }
                        if(key.equals("count")) {
                            long idx = Math.round((double)map.get("count"));
                            locationDto.setCount(idx);
                        }
                        if(key.equals("timestamp")) {
                            long timestamp = Math.round((double)map.get("timestamp"));
                            locationDto.setTimestamp(timestamp);
                        }
                    }
                    String jsonStr = gson.toJson(locationDto,LocationDto.class);

                    if (locationDto.getId() <= 14 && locationDto.getId() > 0 ) {
                        try {
                            log.info("fork : " + jsonStr);
                            mqConfigFork.Send(jsonStr);
                        } catch (ConnectException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            log.info("person : " + jsonStr);
                            mqConfigPerson.Send(jsonStr);
                        } catch (ConnectException e) {
                            e.printStackTrace();
                        }
                    }
                }// mq Data Send
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }
}
