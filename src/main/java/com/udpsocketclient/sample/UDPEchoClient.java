package com.udpsocketclient.sample;

import com.udpsocketclient.sample.config.MqConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeoutException;

public class UDPEchoClient {

    private String str;
    private BufferedReader file;
    private static int SERVER_PORT = 3000;
    private static int CLIENT_PORT = 2000;

    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        MqConfig mqConfig = new MqConfig();
        DatagramSocket ds = new DatagramSocket(SERVER_PORT); // 패킷을 전송받을 포트를 열어둔다.
        while(true) { // 무한루프 통해 메시지 계속 전송받는다
            byte[] data = new byte[65508]; // 한번에 받을 수 있는 최대 용량의 데이터 공간은 기본 정보 공간을 제외한 65,508 byte이다
            //따라서 이 공간만큼을 미리 확보해 둔다.
            DatagramPacket dp = new DatagramPacket(data, data.length);//데이터를 전송받을 객체를 생성한다.
            ds.receive(dp); //패킷을 받는다
            System.out.println(dp.getAddress().getHostAddress() +
                    " >> " + new String(dp.getData()).trim());  //전송받은 패킷이 발송된 곳의 IP주소와 내용 출력

            String str = new String(dp.getData()).trim();
            mqConfig.Send(str);

//            System.out.println("[ 확인 ]" + new String(dp.getData()).trim());

        }
    }

}
