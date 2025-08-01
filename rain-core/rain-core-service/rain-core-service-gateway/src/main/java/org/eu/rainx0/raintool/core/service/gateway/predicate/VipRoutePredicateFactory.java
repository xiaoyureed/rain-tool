package org.eu.rainx0.raintool.core.service.gateway.predicate;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 仿造 QueryParamRoutePredicateFactory
 *
 * 在路由中可以使用短写法:
 *
 *      predicates:
 *          - Vip=user,rain
 *
 * 即 user 参数值必须为 rain 才会匹配
 *
 * @author xiaoyu
 * @time 2025/7/29 21:50
 */
@Component
public class VipRoutePredicateFactory extends AbstractRoutePredicateFactory<VipRoutePredicateFactory.Config> {

    public VipRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("param", "value");
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange serverWebExchange) {
                ServerHttpRequest request = serverWebExchange.getRequest();

                String paramVal = request.getQueryParams().getFirst(config.param);

                return StringUtils.isNotBlank(paramVal) && paramVal.equals(config.value);

            }
        };
    }


    @Validated
    @Data
    public static class Config {
        @NotBlank
        private String param;
        @NotBlank
        private String value;
    }
}
