package org.eu.rainx0.raintool.core.starter.mq.kafka.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 16:42
 */
@Data
@ConfigurationProperties(prefix = "rain.kafka")
public class KafkaProps {

    private boolean defaultConfig = true; // 使用默认配置

    private List<Topic> topics = new ArrayList<>();

    @Data
    public static class Topic {
        private String name;
        private Integer partitions = 1;
        private Integer replicas = 1;
    }

    /**
     * kafka 类型, 默认作为消费者使用
     */
    private Type consumerOrProducer = Type.consumer;

    enum Type {
        consumer,
        producer,
    }
}