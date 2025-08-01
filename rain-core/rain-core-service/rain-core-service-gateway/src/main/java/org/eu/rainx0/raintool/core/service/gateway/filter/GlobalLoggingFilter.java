package org.eu.rainx0.raintool.core.service.gateway.filter;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Optional;

import org.eu.rainx0.raintool.core.common.Consts;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
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
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        HttpMethod method = request.getMethod();
        // String path = request.getPath().toString();
        String ip = Optional.ofNullable(request.getRemoteAddress()).map(InetSocketAddress::getHostString).orElse("");
        long start = System.currentTimeMillis();

        log.debug(";; [{}] {} {}", method.name().toUpperCase(), uri.toString(), ip);

        ServerHttpRequest requestNew = request.mutate()
            //将获取的真实ip存入header微服务方便获取
            .header(Consts.Web.Headers.IP, ip)
            .build();
        return chain
            .filter(exchange.mutate().request(requestNew).build())
            .doFinally(signalType -> {
                long end = System.currentTimeMillis();
                long du = end - start;
                log.debug(";; [{}] {} end, du = {}ms", method.name().toUpperCase(), uri.toString(), du);
            });
    }

    @Override
    public int getOrder() {
        return FilterConsts.GLOBAL_LOGGING_FILTER_ORDER;
    }
}