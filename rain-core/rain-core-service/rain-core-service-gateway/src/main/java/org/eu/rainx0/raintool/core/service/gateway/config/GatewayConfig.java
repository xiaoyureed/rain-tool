package org.eu.rainx0.raintool.core.service.gateway.config;

import java.util.List;

import org.eu.rainx0.raintool.core.service.gateway.listener.RefreshRoutesListener;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 17:03
 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfig {

    private static final String MAX_AGE = "18000L";

    /**
     * 跨域:
     * <p>
     * - 每个 controller 中标注 @CrossOrigin
     * - 配置文件中通过 globalcors 配置
     * <p>
     * 用WebFilter 更好
     */
//    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("*"));
        // config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        // config.setAllowedHeaders(List.of("origin", "content-type", "accept", "authorization", "cookie"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));


        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", config);


        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }

    /**
     * Gateway 跨域处理
     */
    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();

            if (CorsUtils.isCorsRequest(request)) {
                HttpHeaders requestHeaders = request.getHeaders();
                ServerHttpResponse response = ctx.getResponse();
                HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();

                HttpHeaders responseHeaders = response.getHeaders();
                responseHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());
                responseHeaders.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.getAccessControlRequestHeaders());

                if (requestMethod != null) {
                    responseHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());
                }

                responseHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                responseHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
                responseHeaders.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);

                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }

            }
            return chain.filter(ctx);
        };
    }


    /**
     * 代码中定义路由
     */
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//            .route("r1", r -> r.host("**.baeldung.com")
//                .and()
//                .path("/baeldung")
//                .uri("http://baeldung.com"))
//            .route(r -> r.host("**.baeldung.com")
//                .and()
//                .path("/myOtherRouting")
//                .filters(f -> f.prefixPath("/myPrefix"))
//                .uri("http://othersite.com")
//                .id("myOtherID"))
//            .build();
//    }


}