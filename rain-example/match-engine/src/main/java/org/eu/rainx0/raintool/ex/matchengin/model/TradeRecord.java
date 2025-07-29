package org.eu.rainx0.raintool.ex.matchengin.model;

import java.math.BigDecimal;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 成交记录 (每成交一次, 产生一条记录)
 * @author xiaoyu
 * @time 2025/7/21 13:14
 */
@Data
@Accessors(chain = true)
public class TradeRecord {
    private String symbol;
    private BigDecimal amount; // 成交数量
    private BigDecimal price; // 成交价格
    private Integer direction;
    private String takerOrderId; // 买方委托单 Id
    private String makerOrderId; // 卖方委托单 Id
    private BigDecimal takerTurnover; // 买方成交额
    private BigDecimal makerTurnover; // 卖方成交额
    private long time; // 成交时间
}
