package org.eu.rainx0.raintool.ex.matchengin.model;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import lombok.Data;

/**
 * 竞价订单簿
 * 显示在页面上有两部分:
 *  卖盘 (价格从高到低), 越低越容易成交
 *  买盘 (价格也从高到低), 越高越容易成交
 *
 * 两盘中间的汇聚价格就是盘口价
 *
 * @author xiaoyu
 * @time 2025/7/20 16:16
 */
@Data
public class OrderBook {
    /**
     * 卖盘
     * 卖出的限价交易, 价格从低到高排序 (要价越低, 越容易成交) (展示的时候, 要逆序展示)
     */
    private TreeMap<BigDecimal, MergedOrders> asks; // ask 要价
    /**
     * 买盘
     * 买入的限价交易, 价格从高到低排序 (出价越高, 越容易成交)
     * key: 价格
     * value: 同价格的订单 (按照时间从早到迟排序)
     */
    private TreeMap<BigDecimal, MergedOrders> bids; // bid 出价/竞标

    private String symbol; // 币种/标的
    private int scale; // 币种精度
    private int baseScale;// 基币的精度
    private DateTimeFormatter dateTimeFormatter;

    private TradePlate buyTradePlate; // 买方盘口 (这两项应该可以省略, asks, bids 可以代替)
    private TradePlate sellTradePlate; // 卖方盘口

    public OrderBook(String symbol, int scale, int baseScale) {
        this.symbol = symbol;
        this.scale = scale;
        this.baseScale = baseScale;
        this.init();
    }

    public OrderBook(String symbol) {
        this(symbol, 4,4);
    }

    public void init() {
        this.bids = new TreeMap<>(Comparator.reverseOrder());
        this.asks = new TreeMap<>(Comparator.naturalOrder());
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        buyTradePlate = new TradePlate(OrderDirection.BUY, symbol);
        sellTradePlate = new TradePlate(OrderDirection.SELL, symbol);
    }

    public TreeMap<BigDecimal, MergedOrders> get(OrderDirection orderDirection) {
        // 若处理的 Order 是买单, 返回卖盘(asks), 反之返回买盘(bids)
        return orderDirection == OrderDirection.BUY ? asks : bids;
    }

    public void add(Order order) {
        MergedOrders mergedOrders = get(OrderDirection.of(order.getOrderDirection())).get(order.getPrice());
        if (mergedOrders == null) {
            mergedOrders = new MergedOrders();
        }
        mergedOrders.add(order);
    }

    public void cancel(Order order) {
        TreeMap<BigDecimal, MergedOrders> orderBook = get(OrderDirection.of(order.getOrderDirection()));
        MergedOrders mergedOrders = orderBook.get(order.getPrice());
        if (mergedOrders == null) {
            return;
        }
        int size = mergedOrders.remove(order);
        if (size == 0) {
            orderBook.remove(order.getPrice());
        }
    }
}
