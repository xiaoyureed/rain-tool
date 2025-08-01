package org.eu.rainx0.raintool.ex.mqtt;

import org.eu.rainx0.raintool.core.starter.mq.mqtt.inbound.MessageInboundHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/8/1 17:16
 */
@Component
public class MsgHandler implements MessageInboundHandler {
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        Object payload = message.getPayload();
        System.out.println("receive: " + payload);
    }
}
