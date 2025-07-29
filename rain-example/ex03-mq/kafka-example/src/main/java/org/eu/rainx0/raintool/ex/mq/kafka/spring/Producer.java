package org.eu.rainx0.raintool.ex.mq.kafka.spring;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/7/22 14:31
 */
@Component
public class Producer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String message) throws Exception {
        CompletableFuture<SendResult<String, String>> fu = kafkaTemplate.send(topic, message);

        SendResult<String, String> sent = fu.get();
        RecordMetadata meta = sent.getRecordMetadata();
        if (meta!=null) {
            ProducerRecord<String, String> producerRecord = sent.getProducerRecord();
            System.out.println("msg sent: " + producerRecord);
        }
    }


}
