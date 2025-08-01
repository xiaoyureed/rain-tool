package org.eu.rainx0.raintool.core.starter.mq.mqtt.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import java.util.Optional;

/**
 * 进站消息(被订阅) -> mqttInboundAdaptor -> mqttInboundChannel -> mqttInboundHandler
 * 出站消息 ->mqttOutboundChannel -> mqttOutboundHandler  ->  mqtt Broker
 *
 * @author xiaoyu
 * @time 2025/8/1 16:55
 */
@Configuration
public class MqttConfig {

    @Autowired
    MqttProps mqttProps;

    @Bean
    MqttPahoClientFactory mqttPahoClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();

        MqttConnectOptions options = new MqttConnectOptions();
        Optional.ofNullable(mqttProps.getUsername()).ifPresent(options::setUserName);
        Optional.ofNullable(mqttProps.getPassword()).ifPresent(pwd -> {
                options.setPassword(pwd.toCharArray());
        });

        options.setCleanSession(true);
        options.setServerURIs(new String[]{mqttProps.getBrokerUrl()});

        //设置自动重连 + 断线重连监听
//        options.setAutomaticReconnect(true);
        // 每 10s 尝试重连
//        options.setKeepAliveInterval(10_000);


        factory.setConnectionOptions(options);
        return factory;
    }
}
