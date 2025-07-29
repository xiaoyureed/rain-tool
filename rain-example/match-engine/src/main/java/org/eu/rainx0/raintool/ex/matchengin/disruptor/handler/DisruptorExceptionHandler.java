package org.eu.rainx0.raintool.ex.matchengin.disruptor.handler;

import org.eu.rainx0.raintool.ex.matchengin.disruptor.OrderEvent;
import org.springframework.stereotype.Component;

import com.lmax.disruptor.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaoyu
 * @time 2025/7/20 11:36
 */
@Component
@Slf4j
public class DisruptorExceptionHandler implements ExceptionHandler<OrderEvent> {
    @Override
    public void handleEventException(Throwable throwable, long l, OrderEvent o) {
        log.error(";;Disruptor error when handle event, ex: {}, seq: {}, event: {}", throwable.getMessage(), l, o);
    }

    @Override
    public void handleOnStartException(Throwable throwable) {
        log.error(";;Disruptor error when start", throwable);
    }

    @Override
    public void handleOnShutdownException(Throwable throwable) {
        log.error(";;Disruptor error when shutdown", throwable);
    }
}
