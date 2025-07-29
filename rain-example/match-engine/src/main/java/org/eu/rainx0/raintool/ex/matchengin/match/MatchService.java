package org.eu.rainx0.raintool.ex.matchengin.match;

import org.eu.rainx0.raintool.ex.matchengin.model.Order;
import org.eu.rainx0.raintool.ex.matchengin.model.OrderBook;

/**
 * @author xiaoyu
 * @time 2025/7/20 19:47
 */
public interface MatchService {
    void match(Order order, OrderBook orderBook);
}
