package org.eu.rainx0.raintool.ex.matchengin.model;

import java.math.BigDecimal;
import java.util.LinkedList;

import lombok.Data;

/**
 * 盘口
 * @author xiaoyu
 * @time 2025/7/21 13:18
 */
@Data
public class TradePlate {
    private LinkedList<TradePlateItem> items;
    private int maxDepth = 100; // 最大深度
    private OrderDirection direction;
    private String symbol;

    public TradePlate(OrderDirection direction, String symbol) {
        this.direction = direction;
        this.symbol = symbol;
        items = new LinkedList<>();
    }

    public void remove(Order maker, BigDecimal turnoverAmount) {

    }
}
