package org.eu.rainx0.raintool.ex.matchengin.disruptor;

import java.io.Serializable;

import org.eu.rainx0.raintool.ex.matchengin.model.Order;

import lombok.Data;

/**
 * @author xiaoyu
 * @time 2025/7/20 11:41
 */
@Data
public class OrderEvent implements Serializable {
    private final long timestamp;

    private transient Order source;

    public OrderEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public OrderEvent(Order source) {
        this();
        this.setSource(source);
    }
}
