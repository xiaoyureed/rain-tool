package org.eu.rainx0.raintool.core.starter.mq.mqtt.config;

import lombok.extern.slf4j.Slf4j;
import org.eu.rainx0.raintool.core.starter.mq.mqtt.inbound.MessageInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * 配置消息如何进站, 如何处理
 *
 * @author xiaoyu
 * @time 2025/8/1 17:02
 */
@Configuration
@Slf4j
public class MqttInboundConfig {

    @Autowired MqttProps mqttProps;

    @Autowired
    private ApplicationContext appContext;

    // 应用获取消息的通道
    @Bean
    MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    // 订阅topic
    // 收到消息后, 通过mqttInboundChannel 发送到 msg Handler 处理
    @Bean
    MessageProducer mqttInboundAdaptor(MqttPahoClientFactory clientFactory) {
        MqttPahoMessageDrivenChannelAdapter producer = new MqttPahoMessageDrivenChannelAdapter(
                mqttProps.getBrokerUrl(),
                mqttProps.getClientId() + "-inbound",
                clientFactory,
                mqttProps.getSubTopics()
        );

        if (log.isDebugEnabled()) {
            log.debug(";;Subscribing to MQTT topics: {}", (Object[]) mqttProps.getSubTopics());
        }

        producer.setQos(1);
        producer.setConverter(new DefaultPahoMessageConverter());
        // 将 producer的输出 channel 接到我们的 mqttInboundChannel
        producer.setOutputChannel(mqttInboundChannel());

        // app 启动,自动连接到 broker 并开始订阅指定的主题, 默认就是 true
        producer.setAutoStartup(true);

        return producer;
    }

    // 处理器
    @Bean
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    MessageHandler mqttInboundHandler() {
        try {
            return appContext.getBean(MessageInboundHandler.class);
        } catch (Exception e) {
            throw new RuntimeException(";;No MessageInboundHandler bean found", e);
        }

    }

}
