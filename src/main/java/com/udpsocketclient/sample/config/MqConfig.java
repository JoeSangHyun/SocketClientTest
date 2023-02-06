package com.udpsocketclient.sample.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class MqConfig {
    // RabbitMQ Exchange and Rounting and Queue Setting
    public final static String EXCHANGE_NAME = "Sample";
    public final static String ROUTING_KEY = "sample.routingkey.#";
    public final static String QUEUE_NAME = "sample.queue";

    public void Send(String str) throws IOException, TimeoutException, InterruptedException {
        MqClientConfig client = new MqClientConfig();
        Channel channel = client.getChannel();

        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties("application/json",null,null,null,null,null,null,null,null,null,null,null,null,null);

        // Queue Setting
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        String message = str;
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, basicProperties, message.getBytes());
        log.info(" [x] MQ Set '" + message + "'");
        Thread.sleep(10);

        client.close();
    }
}
