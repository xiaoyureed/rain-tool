package org.eu.rainx0.raintool.ex.matchengin.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

/**
 * @author xiaoyu
 * @time 2025/7/20 21:08
 */
@Data
@Table("entrust_order")
public class EntrustOrder {
    private String id;
    private String userId;
    private String symbol;
    private BigDecimal volume; // 总数量
    private BigDecimal price; // 价格
    private BigDecimal dealAmount; // 已成交数量
    private BigDecimal amount; // 委托总额
    private BigDecimal fee; // 交易手续费
    private BigDecimal feeRate; // 费率
    private Integer contractUnit; // 合约单位
    private Integer status;
    private LocalDateTime createdAt;
    private Integer direction; // 1: 买 2: 卖
}
