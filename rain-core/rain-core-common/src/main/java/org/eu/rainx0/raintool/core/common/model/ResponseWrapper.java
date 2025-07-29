package org.eu.rainx0.raintool.core.common.model;

import java.io.Serializable;
import java.util.List;

import org.eu.rainx0.raintool.core.common.CodeEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 08:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "Response wrapper", description = "All of the apis shall return this dto", example = "ResponseWrapper.ok()")
@Accessors(chain = true)
public class ResponseWrapper<T> implements Serializable {

    @Schema(description = "response code")
    private String code;

    @Schema(description = "response error message")
    private String error;

    @Schema(description = "the real data")
    private T data;

    @Schema(description = "pagination info")
    private IPageInfo pageInfo;

    public static <T> ResponseWrapper<T> ok() {
        return new ResponseWrapper<T>()
            .setCode(CodeEnum.success.getCode());
    }

    public static <T> ResponseWrapper<T> ok(T data) {
        return new ResponseWrapper<T>()
            .setCode(CodeEnum.success.getCode())
            .setData(data);
    }

    public static <T> ResponseWrapper<List<T>> ok(List<T> data, IPageInfo pageParam) {
        return new ResponseWrapper<List<T>>()
            .setCode(CodeEnum.success.getCode())
            .setData(data)
            .setPageInfo(pageParam);
    }

    public static <T> ResponseWrapper<T> error(String code, String error) {
        return new ResponseWrapper<T>()
            .setCode(code)
            .setError(error);
    }

    public static <T> ResponseWrapper<T> error(CodeEnum errorEnum) {
        return new ResponseWrapper<T>()
            .setCode(errorEnum.getCode())
            .setError(errorEnum.getDesc());
    }

    public static <T> ResponseWrapper<T> systemError(String error) {
        return new ResponseWrapper<T>()
            .setCode(CodeEnum.system_error.getCode())
            .setError(error);
    }

    public static <T> ResponseWrapper<T> bizError(String bizError) {
        return new ResponseWrapper<T>()
            .setCode(CodeEnum.biz_error.getCode())
            .setError(bizError);
    }
}
