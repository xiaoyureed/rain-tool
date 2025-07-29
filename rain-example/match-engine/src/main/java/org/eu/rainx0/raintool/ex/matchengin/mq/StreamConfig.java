package org.eu.rainx0.raintool.ex.matchengin.mq;

import java.util.function.Consumer;

import org.eu.rainx0.raintool.ex.matchengin.disruptor.DisruptorTemplate;
import org.eu.rainx0.raintool.ex.matchengin.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaoyu
 * @time 2025/7/20 16:08
 */
@Configuration
@Slf4j
public class StreamConfig {

    @Autowired
    private DisruptorTemplate disruptorTemplate;

    @Bean
    public Consumer<Order> orderHandler() {
        return order -> {
            disruptorTemplate.publish(order); // this line shall trigger the OrderEvent
            log.debug(";;Got order, published into disruptor");
        };
    }
}
