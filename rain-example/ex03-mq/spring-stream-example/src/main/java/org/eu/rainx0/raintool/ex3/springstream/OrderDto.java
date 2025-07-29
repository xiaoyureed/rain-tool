package org.eu.rainx0.raintool.ex3.springstream;

import lombok.Data;

/**
 * @author xiaoyu
 * @time 2025/7/20 10:52
 */
@Data
public class OrderDto {
    private String orderId;
    private String userId;
    private boolean risk;
}
