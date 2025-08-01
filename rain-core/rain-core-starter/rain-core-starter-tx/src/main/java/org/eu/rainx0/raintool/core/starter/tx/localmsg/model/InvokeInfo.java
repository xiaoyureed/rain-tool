package org.eu.rainx0.raintool.core.starter.tx.localmsg.model;

import lombok.Data;

/**
 * @author xiaoyu
 * @time 2025/7/31 11:36
 */
@Data
public class InvokeInfo {
    private String clazz;
    private String method;
    private String paramTypes; // json 格式
    private String paramValues; // json 格式
}
