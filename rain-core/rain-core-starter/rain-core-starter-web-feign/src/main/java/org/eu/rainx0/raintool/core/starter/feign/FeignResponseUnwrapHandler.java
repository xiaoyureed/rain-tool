package org.eu.rainx0.raintool.core.starter.feign;

import org.apache.commons.lang3.StringUtils;
import org.eu.rainx0.raintool.core.common.Consts;
import org.eu.rainx0.raintool.core.starter.web.util.ServletTools;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class FeignResponseUnwrapHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.debug(";; " + this.getClass().getSimpleName() + "is in use");
        return true;
    }

    @Override
    public Object beforeBodyWrite(
        Object body,
        MethodParameter returnType,
        MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType,
        ServerHttpRequest request, ServerHttpResponse response
    ) {
        // Check if this request is the internal one
        boolean feign = StringUtils.isNotBlank(
            ServletTools.getRequestHeader(Consts.Web.Headers.FEIGN_FLAG)
        );

        if (feign) {
            return body;
        }

        // todo
        return body;

    }
}