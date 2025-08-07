package org.eu.rainx0.raintool.core.starter.mq.mqtt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Set how the messages are published.
 *
 * @author xiaoyu
 * @time 2025/8/1 18:09
 */
@Configuration
public class MqttOutboundConfig {

    @Autowired
    private MqttProps mqttProps;

    @Autowired
    private MqttPahoClientFactory clientFactory;

    // MqttMessageSender 发送的消息, 会通过这个 Channel, 发送到 mqttOutboundHandler
    //然后被发送到 mqtt Broker
    @Bean
    MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    // outbound channel 中的消息会被这里的 Handler 处理, 发送到 mqtt Broker
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    MessageHandler mqttOutboundHandler() {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(
                mqttProps.getBrokerUrl(),
                mqttProps.getClientId() + "-outbound",
                clientFactory
        );

        handler.setDefaultQos(1);
        handler.setDefaultTopic("default");
        handler.setAsync(true);

        return handler;
    }
}
