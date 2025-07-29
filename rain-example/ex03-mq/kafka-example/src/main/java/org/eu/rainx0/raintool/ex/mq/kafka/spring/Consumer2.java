package org.eu.rainx0.raintool.ex.mq.kafka.spring;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;

/**
 * @author xiaoyu
 * @time 2025/7/24 17:04
 */
@Configuration
public class Consumer2 {

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Bean
    public KafkaMessageListenerContainer<String, String> demoListenerContainer() {
        // 创建container配置参数，并指定要监听的 topic 名称
        ContainerProperties properties = new ContainerProperties("test");
        // 设置消费者组名称
        properties.setGroupId("group2");
        // 设置监听器监听 kafka 消息
        properties.setMessageListener(new MessageListener<String, String>() {
            @Override
            public void onMessage(ConsumerRecord<String, String> record) {
                System.out.println("消息：" + record);
            }
        });
        return new KafkaMessageListenerContainer<>(consumerFactory, properties);
    }

}
