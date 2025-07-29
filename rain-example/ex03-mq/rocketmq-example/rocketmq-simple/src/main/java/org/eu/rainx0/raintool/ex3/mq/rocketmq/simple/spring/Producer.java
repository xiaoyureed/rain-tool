package org.eu.rainx0.raintool.ex3.mq.rocketmq.simple.spring;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/7/18 14:49
 */
@Component
public class Producer {

    @Autowired
    private RocketMQTemplate template;

    public void send(String topic, String msg) {
        template.convertAndSend(topic, msg);

    }

    public void send(String topic, String tag, String msg) {
        template.convertAndSend(topic + ":" + tag, msg);
    }

    /**
     * 具体事务逻辑查看定义的 TransactionListenerImpl
     */
    public void sendInTransaction(String topic, String text) {
        // Message message = new Message(topic, msg.getBytes(StandardCharsets.UTF_8));
        Message<String> message = MessageBuilder
            .withPayload(text)
            .setHeader(RocketMQHeaders.TRANSACTION_ID, "tx-" + System.currentTimeMillis())
            // .setHeader(RocketMQHeaders.TAGS, "tagA")
            // .setHeader(RocketMQHeaders.KEYS, "key")
            // .setHeader("MyProps", "hi")
            .build();
        template.sendMessageInTransaction(topic, message, null);
    }
}
