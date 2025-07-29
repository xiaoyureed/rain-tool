package org.eu.rainx0.raintool.core.common.exception;

import org.eu.rainx0.raintool.core.common.CodeEnum;

import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 08:45
 */
@Data
public class SystemException extends RuntimeException{
    private final String code;
    private final String desc;

    public SystemException(CodeEnum code) {
        super(code.getDesc());
        this.code = code.getCode();
        this.desc = code.getDesc();
    }

    public SystemException(String desc) {
        super(desc);
        this.code = CodeEnum.biz_error.getCode();
        this.desc = desc;
    }
}
