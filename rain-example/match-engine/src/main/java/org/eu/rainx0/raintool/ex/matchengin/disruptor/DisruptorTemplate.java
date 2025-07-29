package org.eu.rainx0.raintool.ex.matchengin.disruptor;

import org.eu.rainx0.raintool.ex.matchengin.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

/**
 * @author xiaoyu
 * @time 2025/7/20 14:15
 */
@Component
public class DisruptorTemplate {
    private static final EventTranslatorOneArg<OrderEvent, Order> TRANSLATOR = new EventTranslatorOneArg<OrderEvent, Order>() {
        @Override
        public void translateTo(OrderEvent orderEvent, long l, Order order) {
            orderEvent.setSource(order);
        }
    };

    @Autowired
    private RingBuffer<OrderEvent> ringBuffer;

    public void publish(Order order) {
        ringBuffer.publishEvent(TRANSLATOR, order);
    }
}
