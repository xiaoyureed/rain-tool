package org.eu.rainx0.raintool.ex.matchengin.disruptor.handler;

import org.apache.commons.lang3.StringUtils;
import org.eu.rainx0.raintool.ex.matchengin.disruptor.OrderEvent;
import org.eu.rainx0.raintool.ex.matchengin.match.MatchServiceFactory;
import org.eu.rainx0.raintool.ex.matchengin.match.MatchStrategy;
import org.eu.rainx0.raintool.ex.matchengin.model.Order;
import org.eu.rainx0.raintool.ex.matchengin.model.OrderBook;

import com.lmax.disruptor.EventHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 每个 symbol 对应一个 Handler
 *
 * @author xiaoyu
 * @time 2025/7/20 14:53
 */
@Slf4j
public class OrderEventHandler implements EventHandler<OrderEvent> {
    private OrderBook orderBook;

    public OrderEventHandler(OrderBook orderBook) {
        this.orderBook = orderBook;
    }


    @Override
    public void onEvent(
        /**
         * 抢占式, 无锁并发, 每个线程都会将所有 event 一股脑全部抢过来
         * 有可能这个 event 当前 Handler无法处理
         *  (每个 Handler 只能处理指定symbol 的 Order)
         */
        OrderEvent orderEvent,
        long seq, boolean endOfBatch
    ) throws Exception {
        log.info(";; Handling order event");

        Order source = orderEvent.getSource();
        if (StringUtils.equalsIgnoreCase(
            source.getSymbol(),
            orderBook.getSymbol()
        )) {
            MatchServiceFactory.get(MatchStrategy.LIMIT_PRICE).match(source, orderBook);
        }
    }
}
