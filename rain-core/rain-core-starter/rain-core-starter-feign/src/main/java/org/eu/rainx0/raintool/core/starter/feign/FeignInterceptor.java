package org.eu.rainx0.raintool.core.starter.feign;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eu.rainx0.raintool.core.common.Consts;
import org.eu.rainx0.raintool.core.starter.web.util.ServletTools;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Handling the headers to avoid header losing
 */
@Component
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 透传请求头, 防止丢失
        ServletTools.getRequestHeaders().forEach(requestTemplate::header);

        // Set up the feign request flag
        requestTemplate.header(Consts.Web.Headers.FEIGN_FLAG, String.valueOf(true));

        // Set up the request id
        if (StringUtils.isBlank(ServletTools.getRequestHeader(Consts.Web.Headers.REQUEST_ID))) {
            requestTemplate.header(Consts.Web.Headers.REQUEST_ID, String.valueOf(UUID.randomUUID()));
        }
    }
}