package org.eu.rainx0.raintool.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 08:34
 */
@Getter
@AllArgsConstructor
public enum CodeEnum {

    success("0000", "成功"),
    system_error("9999", "系统异常"),
    biz_error("9998", "业务异常"),

    illegal_argument("0001", "参数错误"),
    authentication_error("0002", "认证失败"),
    access_deny("0003", "无权限访问"),
    ;


    private final String code;
    private final String desc;
}
