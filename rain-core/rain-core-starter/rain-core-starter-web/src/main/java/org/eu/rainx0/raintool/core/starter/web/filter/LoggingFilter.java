package org.eu.rainx0.raintool.core.starter.web.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 17:48
 */
@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        String uri = request.getRequestURI();
        String httpMethod = request.getMethod();

        String queryString = request.getQueryString();
        uri = queryString == null ? uri : uri + "?" + queryString;

        String formParams = "";
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (!CollectionUtils.isEmpty(parameterMap)) {
            formParams = parameterMap.entrySet().stream().map(ele -> ele.getKey() + ":" + String.format("[%s]", String.join(",", ele.getValue())))
                .collect(Collectors.joining(", "));
            formParams = String.format("{%s}", formParams);
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        String body = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);

        log.debug("[{}] {}, {} {}", httpMethod, uri, formParams, body);

        try {
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            long du = System.currentTimeMillis() - start;
            log.debug("[{}] {}ms", response.getStatus(), du);
        }
    }
}
