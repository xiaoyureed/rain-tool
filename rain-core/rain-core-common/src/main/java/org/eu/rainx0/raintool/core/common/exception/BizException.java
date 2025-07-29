package org.eu.rainx0.raintool.core.common.exception;

import org.eu.rainx0.raintool.core.common.CodeEnum;

import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/6/29 22:30
 */
@Data
public class BizException extends RuntimeException{
    private final String code;
    private final String desc;

    public BizException(String code, String desc) {
        super(desc);
        this.code = code;
        this.desc = desc;
    }

    public BizException(CodeEnum code) {
        super(code.getDesc());
        this.code = code.getCode();
        this.desc = code.getDesc();
    }

    public BizException(String desc) {
        super(desc);
        this.code = CodeEnum.biz_error.getCode();
        this.desc = desc;
    }
}
