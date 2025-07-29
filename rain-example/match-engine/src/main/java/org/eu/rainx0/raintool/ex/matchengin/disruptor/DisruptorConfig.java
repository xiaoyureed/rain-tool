package org.eu.rainx0.raintool.ex.matchengin.disruptor;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.eu.rainx0.raintool.ex.matchengin.disruptor.handler.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import net.openhft.affinity.AffinityThreadFactory;

/**
 * @author xiaoyu
 * @time 2025/7/20 14:23
 */
@Configuration
public class DisruptorConfig {
    @Autowired
    private DisruptorProps disruptorProps;

    @Bean
    public EventFactory<OrderEvent> eventFactory() {
        return new EventFactory<OrderEvent>() {
            @Override
            public OrderEvent newInstance() {
                return new OrderEvent();
            }
        };
    }

    @Bean
    public ThreadFactory threadFactory() {
        // return new ThreadFactory() {
        //     @Override
        //     public Thread newThread(Runnable r) {
        //         return new Thread(r, "disruptor-thread");
        //     }
        // };

        return new AffinityThreadFactory("Match-handler");
    }

    /**
     * 无锁高效的等待策略
     */
    @Bean
    public WaitStrategy waitStrategy() {
        return new YieldingWaitStrategy();
    }

    @Bean
    public RingBuffer<OrderEvent> ringBuffer(
        ExceptionHandler<OrderEvent> exceptionHandler,
        List<EventHandler<OrderEvent>> orderEventHandlers
    ) {
        Disruptor<OrderEvent> orderEventDisruptor = new Disruptor<>(
            eventFactory(),
            disruptorProps.getRingBufferSize(),
            threadFactory(),
            disruptorProps.isMultiProducer() ? ProducerType.MULTI : ProducerType.SINGLE,
            waitStrategy()
        );

        orderEventDisruptor.setDefaultExceptionHandler(exceptionHandler);

        // 每个 symbol 对应一个 Handler
        orderEventDisruptor.handleEventsWith(orderEventHandlers.toArray(new EventHandler[orderEventHandlers.size()]));

        orderEventDisruptor.start(); // 启动 disruptor开始监听

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            orderEventDisruptor.shutdown();
        }, "DisruptorShutdownThread"));

        return orderEventDisruptor.getRingBuffer();
    }

}
