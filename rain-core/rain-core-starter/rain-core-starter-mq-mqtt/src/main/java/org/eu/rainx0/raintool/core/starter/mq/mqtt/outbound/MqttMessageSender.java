package org.eu.rainx0.raintool.core.starter.mq.mqtt.outbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/8/1 18:51
 */
@Component
public class MqttMessageSender {
    @Autowired
    private MessageOutboundGateway messageOutboundGateway;

    public void sendMsg(String topic, String payload) {
        messageOutboundGateway.sendMsg(topic, payload);
    }

    public void sendMsg(String topic, int qos, String payload) {
        messageOutboundGateway.sendMsg(topic, qos, payload);
    }
}
