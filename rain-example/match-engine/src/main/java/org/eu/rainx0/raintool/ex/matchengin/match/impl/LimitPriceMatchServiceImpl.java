package org.eu.rainx0.raintool.ex.matchengin.match.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eu.rainx0.raintool.ex.matchengin.match.MatchService;
import org.eu.rainx0.raintool.ex.matchengin.match.MatchServiceFactory;
import org.eu.rainx0.raintool.ex.matchengin.match.MatchStrategy;
import org.eu.rainx0.raintool.ex.matchengin.model.MergedOrders;
import org.eu.rainx0.raintool.ex.matchengin.model.Order;
import org.eu.rainx0.raintool.ex.matchengin.model.OrderBook;
import org.eu.rainx0.raintool.ex.matchengin.model.OrderDirection;
import org.eu.rainx0.raintool.ex.matchengin.model.TradePlate;
import org.eu.rainx0.raintool.ex.matchengin.model.TradeRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/7/20 19:56
 */
@Component
public class LimitPriceMatchServiceImpl implements MatchService, InitializingBean {
    @Override
    public void match(Order order, OrderBook orderBook) {

        if (order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        TreeMap<BigDecimal, MergedOrders> prices = orderBook.get(OrderDirection.of(order.getOrderDirection()));

        Pair<List<Order>, List<TradeRecord>> processed = process(order, prices, orderBook.getScale());

        // 若盘口中挂单全部吃完, 仍不满足 Order, 则加入盘口
        if (order.getAmount().compareTo(order.getTurnoverAmount()) > 0) {
            orderBook.add(order);
        }

        List<Order> completedOrders = processed.getKey();
        List<TradeRecord> records = processed.getValue();

        // 发送交易记录

        if (CollectionUtils.isNotEmpty(completedOrders)) {
            // 发送已经成功的Order

            TradePlate tradePlate = order.getOrderDirection() == OrderDirection.BUY.getCode()
                ? orderBook.getBuyTradePlate()
                : orderBook.getSellTradePlate();
            // 发送盘口数据
        }

    }


    private Pair<List<Order>, List<TradeRecord>> process(
        Order order, TreeMap<BigDecimal, MergedOrders> prices,
        int scale
    ) {

        List<Order> completedOrders = new ArrayList<>();
        List<TradeRecord> records = new ArrayList<>();

        Iterator<Map.Entry<BigDecimal, MergedOrders>> priceIt = prices.entrySet().iterator();
        while (priceIt.hasNext()) {
            Map.Entry<BigDecimal, MergedOrders> en = priceIt.next();

            BigDecimal curPrice = en.getKey();

            // 若 Order 是买单, 获取到卖盘(价格:小->大), 按照价格阶梯, 依次匹配处理, 直到 Order 价格 < 阶梯价格, 跳出循环
            if (Objects.equals(order.getOrderDirection(), OrderDirection.BUY.getCode())
                && order.getPrice().compareTo(curPrice) < 0
            ) {
                break;
            }
            // 若 Order 是卖单, 获取到买盘(价格:大->小)
            if (Objects.equals(order.getOrderDirection(), OrderDirection.SELL.getCode())
                && order.getPrice().compareTo(curPrice) > 0
            ) {
                break;
            }

            MergedOrders m = en.getValue(); // 这些挂单是按照创建时间从早到迟排序的
            Iterator<Order> marketOrderIt = m.getOrders().iterator();
            while (marketOrderIt.hasNext()) {
                Order marketOrder = marketOrderIt.next();

                TradeRecord record = genRecord(order, marketOrder, scale);
                records.add(record);

                // 经过一圈吃单后, Order 订单已经完成, 返回
                if (order.isCompleted()) {
                    completedOrders.add(order);
                    return Pair.of(completedOrders, records);
                }

                // 若当前挂单完成了, 从盘口移除
                if (marketOrder.isCompleted()) {
                    completedOrders.add(marketOrder);
                    marketOrderIt.remove();
                }
            }

            // 若当前价格的所有挂单完成了, 将整个 price 移除
            if (m.size() == 0) {
                priceIt.remove();
            }
        }

        return Pair.of(completedOrders, records);
    }

    private TradeRecord genRecord(Order taker, Order maker, int scale) {
        BigDecimal dealPrice = maker.getPrice(); // 成交价格
        BigDecimal curTurnoverAmount = calcTurnoverAmount(taker, maker);
        if (curTurnoverAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        BigDecimal curTurnover = curTurnoverAmount.multiply(dealPrice)
            .setScale(scale, RoundingMode.HALF_UP);

        // 累计成交数量
        taker.setTurnoverAmount(taker.getTurnoverAmount()
            .add(curTurnoverAmount)
        );
        // 累计成交额
        taker.setTurnover(curTurnover);

        maker.setTurnoverAmount(maker.getTurnoverAmount().add(curTurnoverAmount));
        maker.setTurnover(curTurnover);

        return new TradeRecord()
            .setTime(System.currentTimeMillis())
            .setPrice(dealPrice)
            .setAmount(curTurnoverAmount)
            .setSymbol(taker.getSymbol())
            .setDirection(taker.getOrderDirection())
            .setTakerOrderId(taker.getOrderId())
            .setMakerOrderId(maker.getOrderId())
            .setTakerTurnover(taker.getTurnover())
            .setMakerTurnover(maker.getTurnover())
        ;
    }

    private BigDecimal calcTurnoverAmount(Order taker, Order maker) {
        BigDecimal t = taker.getAmount().subtract(taker.getTurnoverAmount()); // 总数量 - 当前成交量
        BigDecimal m = maker.getAmount().subtract(maker.getTurnoverAmount());
        // 返回较小值
        return t.compareTo(m) >= 0 ? m : t;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MatchServiceFactory.register(MatchStrategy.LIMIT_PRICE, this);
    }
}
