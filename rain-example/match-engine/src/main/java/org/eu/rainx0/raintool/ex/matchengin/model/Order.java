package org.eu.rainx0.raintool.ex.matchengin.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 订单 (挂单/吃单)
 * @author xiaoyu
 * @time 2025/7/20 11:45
 */
@Data
@Accessors(chain = true)
public class Order implements Serializable {
    private String orderId;
    private String userId;
    private String symbol; // 币种
    private BigDecimal amount = BigDecimal.ZERO; // 想要交易的总数量 (买入/卖出量)
    private BigDecimal turnoverAmount = BigDecimal.ZERO; // 当前累计成交量
    private BigDecimal turnover = BigDecimal.ZERO; // 当前成交额
    private String coinSymbol; // 单位 (以哪种币作为表示单位)
    private String baseSymbol; // 结算单位 (保留几位有效数字)
    private Integer orderStatus;
    private Integer orderDirection;
    private BigDecimal price = BigDecimal.ZERO; // 挂单价格
    private Long time; // 挂单时间
    private boolean completed; // 是否完成
    private Long completedTime; // 完成时间
    private Long cancelTime; // 取消时间

    private List<OrderDetails> details; // 成交详情
}

