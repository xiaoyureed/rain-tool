package org.eu.rainx0.raintool.core.service.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
/**
 * 辨析:
 * GatewayFilter 可以绑定到具体某个路由 (当然必须通过 AbstractGatewayFilterFactory 骨架来生成 Filter)
 *      通过 spring.cloud.gateway.filter.routes.*.*.filters 中绑定到具体路由
 * GlobalFilter 不和路由绑定, 全局生效
 *
 * @author xiaoyu
 * @time 2025/7/12 17:48
 */
// @Component
public class JwtAuthFilterFactory extends AbstractGatewayFilterFactory<JwtAuthFilterFactory.Config> {

    public JwtAuthFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Get token from the request header
            ServerHttpRequest request = exchange.getRequest();

            // parse and checking




            return chain.filter(exchange);
        };
    }

    public static class Config {
        // 如果需要配置参数，可以在这里定义
    }
}

