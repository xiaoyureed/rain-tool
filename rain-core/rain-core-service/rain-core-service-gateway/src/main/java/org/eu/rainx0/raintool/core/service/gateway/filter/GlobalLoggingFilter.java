package org.eu.rainx0.raintool.core.service.gateway.filter;

import org.eu.rainx0.raintool.core.common.Consts;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 17:24
 */
@Slf4j
@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        String path = exchange.getRequest().getPath().toString();
        String ip = exchange.getRequest().getRemoteAddress().getHostString();

        log.debug(";;Request path: {}, {}, client ip: {}", url, path, ip);

        ServerHttpRequest request = exchange.getRequest().mutate()
            //将获取的真实ip存入header微服务方便获取
            .header(Consts.Web.Headers.IP, ip)
            .build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return FilterOrderConsts.GLOBAL_LOGGING_FILTER_ORDER;
    }
}