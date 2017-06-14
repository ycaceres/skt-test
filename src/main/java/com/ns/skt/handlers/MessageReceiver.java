package com.ns.skt.handlers;

import com.google.gson.Gson;
import com.ns.skt.domain.Message;
import com.ns.skt.service.RabbitMQService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile("receiver")
public class MessageReceiver implements CommandLineRunner {
    private RabbitMQService rabbitMQService;

    @Autowired
    public MessageReceiver(RabbitMQService rabbitMQService) {
        this.rabbitMQService = rabbitMQService;
    }

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("");
        System.out.println("====================================================");
        System.out.println("==  THIS IS THE RECEIVER                          ==");
        System.out.println("====================================================");
        System.out.println("");

        Consumer consumer = new DefaultConsumer(this.rabbitMQService.getChannel()) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String messageJSON = new String(body, "UTF-8");
                Message message = new Gson().fromJson(messageJSON, Message.class);
                System.out.println("Received message: " + message.getTimestamp() + " - " + message.getMessage());
            }
        };

        this.rabbitMQService.setConsumer(consumer);
    }
}
