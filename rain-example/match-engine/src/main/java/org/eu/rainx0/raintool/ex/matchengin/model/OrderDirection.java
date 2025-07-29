package org.eu.rainx0.raintool.ex.matchengin.model;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.helpers.MessageFormatter;

import lombok.Getter;

/**
 * @author xiaoyu
 * @time 2025/7/20 16:37
 */
@Getter
public enum OrderDirection {
    BUY(1, "buy"),
    SELL(2, "sell"),

    ;

    private final Integer code;
    private final String desc;

    OrderDirection(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderDirection of(int code) {
        for (OrderDirection value : values()) {
            if (code == value.code) {
                return value;
            }
        }
        String message = MessageFormatter.format(
            "Illegal code: {}. Only {} allowed", code,
            Arrays.stream(values()).map(OrderDirection::getCode).collect(Collectors.toList())).getMessage();
        throw new RuntimeException(message);
    }
}
