package com.udpsocketclient.sample.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MqClientConfig {
    private Connection connection = null;
    private Channel channel = null;
    private final static String SERVER_HOST = "localhost";
    private final static int SERVER_PORT = 5672;
    private final static String USER_NAME = "guest";
    private final static String USER_PASSWORD = "guest";


    public Channel getChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(SERVER_HOST);
        factory.setPort(SERVER_PORT);
        factory.setUsername(USER_NAME);
        factory.setPassword(USER_PASSWORD);
        factory.setVirtualHost("/");
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();

        return this.channel;
    }

    public void close() throws IOException, TimeoutException {
        this.channel.close();
        this.connection.close();
    }
}
