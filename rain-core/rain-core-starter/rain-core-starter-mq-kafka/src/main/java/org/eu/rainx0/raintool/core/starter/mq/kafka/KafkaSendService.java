package org.eu.rainx0.raintool.core.starter.mq.kafka;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 16:47
 */
@Component
public class KafkaSendService<K, V> {
    @Autowired
    private KafkaTemplate<K, V> kafkaTemplate;

    /**
     * 发送消息
     */
    public CompletableFuture<SendResult<K, V>> send(String topic, V message, Optional<ProducerListener<K, V>> productListener) {
        if (!StringUtils.hasText(topic)) {
            throw new IllegalArgumentException("Topic cannot be empty.");
        }

        if (message == null) {
            throw new IllegalArgumentException("Message data cannot be empty.");
        }

        productListener.ifPresent(producerListener -> kafkaTemplate.setProducerListener(producerListener));

        CompletableFuture<SendResult<K, V>> future = kafkaTemplate.send(topic, message);
        return future;
    }

    /**
     * 要求:
     * 预先配置 props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my-transaction-id"); 设置 tx id 前缀, 以启用事务支持
     * 或者 DefaultKafkaProducerFactory.setTransactionIdPrefix("tx-"); // 启用事务支持
     *
     * OperationsCallback<K, V, T>，其中 T 是返回值类型。
     * 返回值 T（这里是 Boolean）会被 executeInTransaction 方法返回。
     * true / 非空值
     *      表示事务操作成功，可以提交事务
     * 抛出异常
     *      表示事务失败，会触发事务回滚
     * 返回null   不推荐(意义不明确)
     */
    public void sendTx(String topic, V message) {
        Boolean ok = kafkaTemplate.executeInTransaction((KafkaOperations.OperationsCallback<K, V, Boolean>) opts -> {
            // send msg
            opts.send(topic, message);
            opts.send("topic2", (V) "hello");

            // other logic.
            // May produce error, and then the whole tx rollback

            return true;
        });
    }
}
