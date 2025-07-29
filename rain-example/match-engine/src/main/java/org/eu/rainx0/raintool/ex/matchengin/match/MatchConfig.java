package org.eu.rainx0.raintool.ex.matchengin.match;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eu.rainx0.raintool.ex.matchengin.disruptor.OrderEvent;
import org.eu.rainx0.raintool.ex.matchengin.disruptor.handler.OrderEventHandler;
import org.eu.rainx0.raintool.ex.matchengin.model.OrderBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import com.lmax.disruptor.EventHandler;

/**
 * @author xiaoyu
 * @time 2025/7/20 19:21
 */
@Configuration
public class MatchConfig {
    @Autowired
    private MatchProps matchProps;

    @Bean
    public List<EventHandler<OrderEvent>> orderEventHandlers() {
        Map<String, MatchProps.CoinScale> symbols = matchProps.getSymbols();

        if (CollectionUtils.isEmpty(symbols)) {
            throw new RuntimeException("No symbols configured");
        }

        return symbols.entrySet().stream().map(en -> {
            String sy = en.getKey();
            MatchProps.CoinScale scale = en.getValue();
            OrderBook orderBook = scale == null
                ? new OrderBook(sy)
                : new OrderBook(sy, scale.getScale(), scale.getBaseScale());
            return new OrderEventHandler(orderBook);
        }).collect(Collectors.toList());
    }

}
