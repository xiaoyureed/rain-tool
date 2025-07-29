package org.eu.rainx0.raintool.core.starter.webstatic;

import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 13:39
 */
@Configuration
@ConditionalOnProperty(prefix = "core.starter.webstatic", name = "enabled", havingValue = "true")
public class StaticResourceConfiguration implements WebMvcConfigurer {
    /**
     * To serve the nextjs static resources directly without going through the backend
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

    /**
     * 为静态页面的路由自动补全 .html 然后重定向
     * (
     * 解释: nextjs在打包进 springboot 后, 作为springboot 里的静态资源,
     * 整个应用只认识 xxx.html. 当然如果前后分离开发时, nextjs 的服务器是认识 xxx的
     * )
     * 开发阶段可以没有, 但是打包时要加上此配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                boolean isApiHandle = handler instanceof HandlerMethod;
                String servletPath = request.getServletPath();

                /**
                 * 请求路径满足:
                 * - 非 controller 中的 handler
                 * - 非根路径
                 * - 没有后缀 (类似 .html 这样的后缀)
                 *
                 * 就重定向到 xxx.html
                 */
                if (!isApiHandle && !"/".contentEquals(servletPath) && FilenameUtils.getExtension(servletPath).isEmpty()) {
                    request.getRequestDispatcher(servletPath + ".html").forward(request, response);
                    return false;
                }

                return true;
            }
        });
    }
}
