package org.eu.rainx0.raintool.core.starter.web.confg;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eu.rainx0.raintool.core.starter.util.BeanTools;
import org.eu.rainx0.raintool.core.starter.web.filter.AuthenticationInterceptor;
import org.eu.rainx0.raintool.core.starter.web.login.CurrentUserArgResolver;
import org.eu.rainx0.raintool.core.starter.web.route.RouteVersionHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${rain.web.static.enabled:false}")
    private boolean staticEnabled;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    /**
     * To serve the nextjs static resources directly without going through the backend
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (staticEnabled) {
            registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
            .excludePathPatterns("/swagger-ui/index.html","/swagger-ui.html", "/webjars/**", "/v3/api-docs", "/v2/api-docs", "/swagger-resources/**", "/doc.html", "/login", "/register");

        if (staticEnabled) {
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

    /**
     * cors configuration
     * 必须配置, 否则 gateway 的 swagger 会在页面报 cors 异常
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .maxAge(18000L)          // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
            .allowedOrigins("*")                 // 设置允许跨域请求的域名
//            .allowedOriginPatterns("*")    // 同上
            .allowedHeaders("*")           // 设置允许的header
            .allowedMethods("*");       //// 设置允许的请求方式
//            .allowCredentials(true);  // 是否允许cookie跨域
    }

    /**
     * 用于向 Spring 的 HTTP 消息转换器（MessageConverters）链中添加一个新的自定义转换器，以支持自定义的 JSON 序列化或反序列化行为
     * 被 Spring 用来处理 @RequestBody 和 @ResponseBody 注解所支持的请求和响应
     * <p>
     * extendMessageConverters(), configureMessageConverters() 区别:
     * 1. 执行时机不同:
     * - extendMessageConverters()是在Spring Boot自动配置的HttpMessageConverters初始化后执行。
     * - configureMessageConverters()是在Spring Boot自动配置的HttpMessageConverters初始化前执行。
     * 2. 配置方式不同:
     * - extendMessageConverters()是追加或者修改默认HttpMessageConverters。
     * - configureMessageConverters()是完全自定义HttpMessageConverters,会覆盖默认的配置。
     * 3. 使用场景不同:
     * - 如果只需要添加或者修改部分消息转换器,推荐使用extendMessageConverters()。
     * - 如果要完全控制HttpMessageConverters的配置,不使用Spring Boot的自动配置,则需要使用configureMessageConverters()。
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // converters.add(...) 会放在列表末尾，优先级较低。你也可以用 add(0, ...) 来插到最前面，提高优先级。
        converters.add(mappingJackson2HttpMessageConverter());
    }

    /**
     * 定制 Spring MVC 如何解析 URL 路径
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
//        给所有 @RestController 中方法添加前缀 /api，无需手写。
//        configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
        // or
//        configurer.addPathPrefix("/api", handlerType -> handlerType.isAnnotationPresent(RestController.class));
    }

    /**
     * mvc 配置 json 格式化
     * <p>
     * 如果仅仅是为了配置 object mapper, 可以省略, 因为 mvc默认就是使用的用户自定义的 Object mapper (如果有自定义 mapper的话)
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter result = new MappingJackson2HttpMessageConverter();

        // mvc 的 json 使用内置的
        //todo
        result.setObjectMapper(BeanTools.objectMapper);

        result.setDefaultCharset(StandardCharsets.UTF_8);
        result.setSupportedMediaTypes(List.of(
            MediaType.APPLICATION_JSON,
            MediaType.TEXT_PLAIN
        ));

        return result;
    }

    /**
     * springmvc 默认已经注册了 HandlerExceptionResolver bean, 可以代替这里
     */
    @Bean
    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {

        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        // 支持 JSON 输出
        resolver.setMessageConverters(List.of(mappingJackson2HttpMessageConverter()));
        resolver.afterPropertiesSet(); // 初始化 resolver 的内部组件
        return resolver;
    }

    /**
     * 自定义的参数解析器, 如自动注入当前登录用户
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgResolver());
    }

    /**
     * WebMvcRegistrations
     * - getRequestMappingHandlerMapping()自定义 URL 映射策略（如 API 版本控制）
     * - getRequestMappingHandlerAdapter() 自定义 HandlerAdapter 行为, 比如，定制参数解析、返回值处理行为（比如支持特殊注解、非标准参数类型）
     * - getExceptionHandlerExceptionResolver() 定制异常处理机制, 处理 @ExceptionHandler 异常映射逻辑。
     */
    @Bean
    public WebMvcRegistrations webMvcRegistrations() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new RouteVersionHandlerMapping();
            }
        };
    }


    //  /**
    //   * swagger 相关的静态资源配置
    //   * 可选, xxx-xxx-ui 依赖里包含了这部分配置了
    //   */
    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {
    //     registry.addResourceHandler("swagger-ui.html")
    //         .addResourceLocations("classpath:/META-INF/resources/");
    //
    //     registry.addResourceHandler("/webjars/**")
    //         .addResourceLocations("classpath:/META-INF/resources/webjars/");
    // }

}
