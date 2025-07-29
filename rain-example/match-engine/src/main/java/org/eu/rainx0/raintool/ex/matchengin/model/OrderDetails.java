package org.eu.rainx0.raintool.ex.matchengin.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author xiaoyu
 * @time 2025/7/20 14:12
 */
@Data
public class OrderDetails {
    private String orderId;
    private BigDecimal price;// 成交价格
    private BigDecimal amount; // 成交数量
    private BigDecimal turnover;// 成交额
    private BigDecimal fee; // 费率
}
