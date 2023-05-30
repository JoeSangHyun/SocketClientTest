package com.udpsocketclient.sample.config;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class MqConfig {
    // RabbitMQ Exchange and Rounting and Queue Setting
    public String EXCHANGE_NAME;
    public String ROUTING_KEY;
    public String QUEUE_NAME;

    public MqConfig (String EXCHANGE_NAME, String ROUTING_KEY, String QUEUE_NAME) {
        this.EXCHANGE_NAME = EXCHANGE_NAME;
        this.ROUTING_KEY = ROUTING_KEY;
        this.QUEUE_NAME = QUEUE_NAME;
    }

    public void Send(String str) throws IOException, TimeoutException, InterruptedException {
        MqClientConfig client = new MqClientConfig();
        Channel channel = client.getChannel();

//        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties("application/json",null,null,null,null,null,null,null,null,null,null,null,null,null);
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties(
                "application/json",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, basicProperties, str.getBytes());
        Thread.sleep(10);
        client.close();
    }
}
