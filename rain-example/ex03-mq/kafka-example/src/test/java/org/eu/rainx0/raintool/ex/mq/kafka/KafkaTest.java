package org.eu.rainx0.raintool.ex.mq.kafka;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

/**
 * @author xiaoyu
 * @time 2025/7/22 17:14
 */
@SpringBootTest(classes = {KafkaTest.Config.class, KafkaAutoConfiguration.class})
public class KafkaTest {
    @Autowired
    private KafkaTemplate<String, String> template;

    @Test
    void testSend() throws Exception {
        CompletableFuture<SendResult<String, String>> fu = template.send("hi", "hi from rain");
        SendResult<String, String> sent = fu.get();
        RecordMetadata meta = sent.getRecordMetadata();
        if (meta == null) {
            System.out.println("send failed");
            return;
        }
        ProducerRecord<String, String> record = sent.getProducerRecord();
        System.out.println("sent: " + record);

    }

    @Configuration
    static class Config {
        @Bean
        public NewTopic topicHi() {

            return TopicBuilder.name("hi")
                .partitions(1)
                .replicas(1)
                .build();
        }

        @Bean
        public NewTopic topicHi2() {
            return new NewTopic("hi2", 1, (short) 1);
        }

    }
}

