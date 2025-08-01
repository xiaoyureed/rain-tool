package org.eu.rainx0.raintool.core.service.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * 辨析:
 * <p>
 * GatewayFilter 必须在配置中绑定到具体某个路由 (当然必须通过 AbstractGatewayFilterFactory 骨架来生成这个 Filter)
 * 通过 spring.cloud.gateway.filter.routes.*.*.filters 中绑定到具体路由
 * 若就是想全局生效, 可以配置到 default-filters 下
 * <p>
 * GlobalFilter 不和路由绑定, 默认就是全局生效
 *
 * @author xiaoyu
 * @time 2025/7/12 17:48
 */
@Component
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {

    public JwtAuthGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Get token from the request header
            ServerHttpRequest request = exchange.getRequest();

            // parse and checking


            return chain.filter(exchange)
                // 在过滤之后, 返回响应之前, 做一些事
                .then(Mono.fromRunnable(() -> {
                    System.out.println("JwtAuthGatewayFilterFactory.apply()");
                }));
        };
    }

    public static class Config {
        // 如果需要配置参数，可以在这里定义
    }
}

