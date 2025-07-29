package org.eu.rainx0.raintool.ex.mq.kafka.spring;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/7/24 17:12
 */
@Component
@KafkaListener(id = "myId", topics = "test")
public class Consumer3 {
    /**
     * 在传递消息时，将使用转换后的消息有效负载类型来确定调用那个方法
     */
    @KafkaHandler
    public void listen(String str) {

    }

    @KafkaHandler
    public void listen(Integer integer) {

    }
}
