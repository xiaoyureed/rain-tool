package org.eu.rainx0.raintool.ex3.springstream.row;

import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author xiaoyu
 * @time 2025/7/20 09:50
 */
public class StreamDemo {
    public static void main(String[] args) {
        DirectChannel channel = new DirectChannel();
        ((SubscribableChannel) channel).subscribe(new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println("Got: " + message.getPayload());
            }
        });

        boolean ok = channel.send(MessageBuilder.withPayload("hello").build());
    }
}
