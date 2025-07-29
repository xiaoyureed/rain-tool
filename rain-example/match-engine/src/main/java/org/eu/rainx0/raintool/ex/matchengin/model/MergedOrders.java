package org.eu.rainx0.raintool.ex.matchengin.model;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Optional;

import lombok.Data;

/**
 * OrderBook 中价格相同的订单集合
 *
 * @author xiaoyu
 * @time 2025/7/20 16:19
 */
@Data
public class MergedOrders {
    private LinkedList<Order> orders = new LinkedList<>();

    public void add(Order order) {
        orders.addLast(order);
    }

    public Optional<Order> get() {
        if (this.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(orders.getFirst());
    }

    public int size() {
        return orders.size();
    }

    public BigDecimal price() {
        return this.get().orElseThrow(() -> new RuntimeException("Empty MergeOrders")).getPrice();
    }

    public BigDecimal totalAmount() {
        return orders.stream()
            .map(Order::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int remove(Order order) {
        this.orders.removeIf(o -> o.getOrderId().equals(order.getOrderId()));
        return this.size();
    }
}
