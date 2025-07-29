package org.eu.rainx0.raintool.core.starter.websecurity.exception.handler;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 捕捉Spring security filter chain 中抛出的未知异常, resolve 到 controller 层
 * (然后被 controller 层全局异常捕获处理)
 * @author: xiaoyu
 * @time: 2025/7/7 21:25
 */
// @Component
// @Slf4j
// @Order(-101) // 保证请求最先进入, 最后出来
public class GlobalSecurityExceptionCatchFilter extends OncePerRequestFilter { // (OncePerRequestFilter 对于转发/重定向 这种内部请求, 不会拦截, 只会拦截外部进来的请求, 也就是只会拦截一次)

    /**
     * 当前位于 servlet Filter 中, 没有进入 controller 层, exception 无法在 @ControllerAdvice 统一处理
     * 必须通过 HandlerExceptionResolver 转一下, 才能在 controller 层catch
     */
    private final HandlerExceptionResolver handlerExceptionResolver;

    public GlobalSecurityExceptionCatchFilter(HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 这里不能再抛出了, 否则 全局异常处理就捕获不到了
            // throw e;

            // here the resolved exception shall be caught in the global exception handler
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }
}
