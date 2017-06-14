package com.ns.skt.handlers;

import com.ns.skt.domain.Message;
import com.ns.skt.service.RabbitMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Profile("sender")
public class MessageSender implements CommandLineRunner {

    private RabbitMQService rabbitMQService;

    @Autowired
    public MessageSender(final RabbitMQService rabbitMQService) {
        this.rabbitMQService = rabbitMQService;
    }

    @Override
    public void run(String... strings) throws Exception {

        String messageTxt = "Message ";
        int messageCount = 0;

        Pattern pattern = Pattern.compile("^\\-messages=(\\d+)$");

        for(String param: strings) {
            Matcher matcher = pattern.matcher(param.trim());
            if(matcher.matches()){
                messageCount = Integer.parseInt(matcher.group(1));
            }
        }

        System.out.println("");
        System.out.println("====================================================");
        System.out.println("==  THIS IS THE SENDER                            ==");
        System.out.println("====================================================");
        System.out.println("==  SENDING " + messageCount + " MESSAGES...");

        for (int i = 0; i < messageCount; i++) {
            Message message = new Message(messageTxt + i);
            this.rabbitMQService.sendMessage(message);
        }

        System.out.println("");

        this.rabbitMQService.close();
    }

}
