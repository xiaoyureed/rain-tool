package org.eu.rainx0.raintool.ex.mq.kafka.spring;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/7/24 16:59
 */
@Component
public class Consumer {
    @KafkaListener(topics = {"test"}, groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void kafkaListener(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        System.out.println(message);
    }

    @KafkaListener(id = "webGroup1", topics = "hello")
    public void onMessage(
        ConsumerRecord<Object, Object> record,
        Acknowledgment ack,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {
        System.out.println("单条消费消息：" + record.topic() + "----" + record.partition() + "----" + record.value());

        // 手动提交 offset
        ack.acknowledge();
    }

    /**
     * errorHandler 不指定listenErrorHandler的情况，使用全局异常
     *     ConsumerAwareListenerErrorHandler/ConsumerAwareErrorHandler
     */
    @KafkaListener(id = "webGroup2", topics = "hello", errorHandler = "xxx")
    public void onMessageBatch(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {
        for (ConsumerRecord<?, ?> record : records) {
            System.out.println("批量消费消息：" + record.topic() + "----" + record.partition() + "----" + record.value());
        }
        ack.acknowledge();
    }

    @KafkaListener(
        topicPartitions = {
            @TopicPartition(topic = "hello", partitions = {"0"}),
            @TopicPartition(topic = "hello", partitionOffsets = {
                @PartitionOffset(partition = "1", initialOffset = "0")// // msgs will be re-consuming every time this listener is initialized
            })
        },
        containerFactory = "partitionsKafkaListenerContainerFactory"
    )

    public void consumePartition() {

    }
}
