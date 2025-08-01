package org.eu.rainx0.raintool.core.starter.tx.localmsg.model;

import java.time.LocalDateTime;

/**
 * @author xiaoyu
 * @time 2025/7/31 11:32
 */
public interface LocalMessage<ID> {
    // primary key
    ID getId();

    // 调用信息
    InvokeInfo getInvokeInfo();

    // 状态 1待执行 2已失败
    int getStatus();

    // 下次重试时间
    LocalDateTime getNextRetryTime();

    // 已重试次数
    int getRetriedTimes();

    // 最大重试次数
    int getMaxRetryTimes();

    // 错误信息
    String getError();


}
