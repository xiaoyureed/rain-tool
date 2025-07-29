package org.eu.rainx0.raintool.core.service.gateway.filter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eu.rainx0.raintool.core.common.CodeEnum;
import org.eu.rainx0.raintool.core.common.Consts;
import org.eu.rainx0.raintool.core.common.model.ResponseWrapper;
import org.eu.rainx0.raintool.core.common.util.JwtTools;
import org.eu.rainx0.raintool.core.service.gateway.config.GatewayProps;
import org.eu.rainx0.raintool.core.service.gateway.util.WebFluxUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 17:09
 */
@Slf4j
@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    private static final String BEARER = "Bearer";

    @Autowired
    private GatewayProps gatewayProps;

    /**
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!gatewayProps.getAuth().isEnabled()) {
            log.warn(";; gateway auth is disabled");
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();

        String url = request.getURI().getPath();
        List<String> whiteList = gatewayProps.getWhiteList();
        if (WebFluxUtils.isPathMatch(whiteList, url)) {
            return chain.filter(exchange);
        }

        // 外部进入的请求，如果包含 x-feign 请求头，认为是非法请求，直接拦截。x-feign 只能用于内部 Feign 间忽略权限使用
        // If the request contains x-feign header, we assume that it's an illegal request
        //(since the x-feign header only appears in internal request)
        String internalFlag = request.getHeaders().getFirst(Consts.Web.Headers.FEIGN_FLAG);
        if (StringUtils.equalsIgnoreCase(internalFlag, "true")) {
            log.error(";;Internal feign request should not be here!");
            ResponseWrapper<?> response = ResponseWrapper.systemError("Illegal request");
            return WebFluxUtils.writeJsonResponse(exchange.getResponse(), response);
        }

        // // allow the websocket request.
        // String webSocketToken =  exchange.getRequest().getHeaders().getFirst(com.google.common.net.HttpHeaders.SEC_WEBSOCKET_PROTOCOL);
        // if (StringUtils.isNotBlank(webSocketToken) && StringUtils.endsWith(webSocketToken,".stomp")) {
        //     return chain.filter(exchange);
        // }

        String token = request.getHeaders().getFirst(Consts.Web.Headers.AUTH);
        if (StringUtils.isBlank(token)) {
            return WebFluxUtils.writeJsonResponse(exchange.getResponse(), ResponseWrapper.systemError("Token missing"));
        }
        if (!StringUtils.startsWithIgnoreCase(token, BEARER)) {
            return WebFluxUtils.writeJsonResponse(exchange.getResponse(), ResponseWrapper.systemError("Unsupported token format"));
        }

        try {
            Claims claims = JwtTools.parseToken(token);
            String username = claims.getSubject();

            ServerHttpRequest filledRequest = exchange.getRequest().mutate()
                .header(Consts.Web.Headers.USER_ID, username).build();

            return chain.filter(exchange.mutate().request(filledRequest).build());
        } catch (Exception e) {
            return WebFluxUtils.writeJsonResponse(exchange.getResponse(), ResponseWrapper.error(CodeEnum.authentication_error));
        }
    }

    @Override
    public int getOrder() {
        return FilterOrderConsts.GLOBAL_AUTH_FILTER_ORDER;
    }


}