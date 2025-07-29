package org.eu.rainx0.raintool.ex3.mq.rocketmq.simple.spring;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/7/18 15:22
 */
@Component
@RocketMQMessageListener(consumerGroup = "my_consumer_group", topic = "topic1")
public class Consumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        System.out.println("Consumer received: " + s);
    }
}
