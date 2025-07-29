package org.eu.rainx0.raintool.ex.mq.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * @author xiaoyu
 * @time 2025/7/22 14:14
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    /**
     * 或者通过 @PostConstruct + DefaultListableBeanFactory 手动注册 topic bean
     */
    @Bean
    public NewTopic topic1() {
        return new NewTopic("topic1",
            // partitions
            1,
            // replicas
            (short) 1
        );
    }

    @Bean
    public NewTopic topic2() {
        return new NewTopic("topic2", 1, (short) 1);
    }

}
