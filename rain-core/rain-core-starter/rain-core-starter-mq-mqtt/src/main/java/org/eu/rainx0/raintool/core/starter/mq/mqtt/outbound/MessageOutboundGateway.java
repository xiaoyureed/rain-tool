package org.eu.rainx0.raintool.core.starter.mq.mqtt.outbound;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * 将内容转换为消息, 发送到指定 channel
 * @author xiaoyu
 * @time 2025/8/1 18:39
 */
@MessagingGateway(
        // 指定默认channel
        defaultRequestChannel = "messageOutboundChannel")
public interface MessageOutboundGateway {

//    // 也可在方法上指定 channel，发送到不同通道
//    @Gateway(requestChannel = "anotherChannel")
//    void sendToAnotherTopic(String message);

    void sendMsg(@Header(MqttHeaders.TOPIC) String topic, String payload);

    void sendMsg(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos,String payload);
}
