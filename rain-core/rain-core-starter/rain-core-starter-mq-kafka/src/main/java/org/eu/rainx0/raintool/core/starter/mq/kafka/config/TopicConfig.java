package org.eu.rainx0.raintool.core.starter.mq.kafka.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 16:45
 */
@Configuration
@EnableConfigurationProperties({
    KafkaProps.class
})
@Slf4j
public class TopicConfig implements InitializingBean {
    @Autowired
    private KafkaProps kafkaProps;

    @Autowired
    private DefaultListableBeanFactory defaultListableBeanFactory;

    private void initTopics() {

        kafkaProps.getTopics().forEach(topic -> {
            defaultListableBeanFactory.registerSingleton(topic.getName(),
                TopicBuilder.name(topic.getName())
                    .partitions(topic.getPartitions())
                    .replicas(topic.getReplicas())
                    .build()
            );

            log.debug(";;Kafka topic [{}] registered", topic.getName());
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initTopics();
    }
}
