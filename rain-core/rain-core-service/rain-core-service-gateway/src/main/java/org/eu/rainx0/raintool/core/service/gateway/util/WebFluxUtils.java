package org.eu.rainx0.raintool.core.service.gateway.util;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.eu.rainx0.raintool.core.common.CodeEnum;
import org.eu.rainx0.raintool.core.common.model.ResponseWrapper;
import org.eu.rainx0.raintool.core.starter.util.BeanTools;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.reactive.resource.ResourceUrlProvider;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 */
public class WebFluxUtils {

    private static final PathMatcher pathMatcher = new AntPathMatcher();
    private static final ResourceUrlProvider resourceUrlProvider = new ResourceUrlProvider();

    public static PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public static ResourceUrlProvider getResourceUrlProvider() {
        return resourceUrlProvider;
    }

    public static Mono<Boolean> isStaticResources(String uri, ServerWebExchange exchange) {
        ResourceUrlProvider resourceUrlProvider = getResourceUrlProvider();
        Mono<String> staticUri = resourceUrlProvider.getForUriString(uri, exchange);
        return staticUri.hasElement();
    }

    /**
     * 判断路径是否与路径模式匹配
     * @param patterns 路径模式数组
     * @param path url
     * @return 是否匹配
     */
    public static boolean isPathMatch(String[] patterns, String path) {
        return isPathMatch(Arrays.asList(patterns), path);
    }

    /**
     * 判断路径是否与路径模式匹配
     * @param patterns 路径模式字符串List
     * @param path url
     * @return 是否匹配
     */
    public static boolean isPathMatch(List<String> patterns, String path) {
        PathMatcher pathMatcher = getPathMatcher();
        for (String pattern : patterns) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    public static Mono<Void> writeJsonResponse(ServerHttpResponse serverHttpResponse, ResponseWrapper<?> responseWrapper) {
        serverHttpResponse.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if (StringUtils.equals(responseWrapper.getCode(), CodeEnum.success.getCode())) {
            serverHttpResponse.setStatusCode(HttpStatus.OK);
        } else {
            serverHttpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
        }

        String jsonResult = BeanTools.toJson(responseWrapper);
        byte[] bytes = jsonResult.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = serverHttpResponse.bufferFactory().wrap(bytes);

        return serverHttpResponse.writeWith(Flux.just(buffer));
    }

    public static boolean isJsonMediaType(String contentType) {
        return MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType) || MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(contentType);
    }

    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     * @return 请求体
     */
    public static String getRequestBody(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyReference = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyReference.set(charBuffer.toString());
        });
        //获取request body
        return bodyReference.get();
    }
}
