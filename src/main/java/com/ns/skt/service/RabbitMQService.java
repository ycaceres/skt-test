package com.ns.skt.service;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RefreshScope
public class RabbitMQService implements InitializingBean {

    @Value("${skt.config.rabbitmq.queue}")
    private String queue;

    @Value("${skt.config.rabbitmq.user}")
    private String user;

    @Value("${skt.config.rabbitmq.pass}")
    private String password;

    @Value("${skt.config.rabbitmq.host}")
    private String host;

    @Value("${skt.config.rabbitmq.port}")
    private String port;

    private final static Gson GSON = new Gson();

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    public Channel getChannel() {
        return this.channel;
    }

    public void sendMessage(Object o) {
        try {
            String jsonMessage = GSON.toJson(o);
            this.channel.basicPublish("", queue, null, jsonMessage.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.channel.close();
            this.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.factory = new ConnectionFactory();
            this.factory.setHost(this.host);
            this.factory.setPort(Integer.parseInt(this.port));
            this.factory.setUsername(this.user);
            this.factory.setPassword(this.password);

            this.connection = this.factory.newConnection();
            this.channel = this.connection.createChannel();
            this.channel.queueDeclare(this.queue, false, false, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setConsumer(Consumer consumer) {
        try {
            this.channel.basicConsume(queue, true, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
