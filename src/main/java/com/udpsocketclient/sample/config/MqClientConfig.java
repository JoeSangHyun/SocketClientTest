package com.udpsocketclient.sample.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class MqClientConfig {
    private Connection connection = null;
    private Channel channel = null;
    private String SERVER_HOST;
    private int SERVER_PORT;
    private String USER_NAME;
    private String USER_PASSWORD;


    public Channel getChannel() throws IOException, TimeoutException {

        readProperties util = new readProperties();

        Properties prop = util.readProperties("application.properties");

        SERVER_HOST =  prop.getProperty("rabbitmq.server.ip");
        SERVER_PORT = Integer.parseInt(prop.getProperty("rabbitmq.server.port"));
        USER_NAME = prop.getProperty("rabbitmq.server.username");
        USER_PASSWORD = prop.getProperty("rabbitmq.server.password");

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
