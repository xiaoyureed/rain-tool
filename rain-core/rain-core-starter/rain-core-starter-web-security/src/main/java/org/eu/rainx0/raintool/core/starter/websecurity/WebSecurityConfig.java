package org.eu.rainx0.raintool.core.starter.websecurity;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eu.rainx0.raintool.core.starter.util.SpringContextTools;
import org.eu.rainx0.raintool.core.starter.websecurity.auth.AnonymousAccess;
import org.eu.rainx0.raintool.core.starter.websecurity.auth.AuthFailedHandler;
import org.eu.rainx0.raintool.core.starter.websecurity.auth.AuthSuccessHandler;
import org.eu.rainx0.raintool.core.starter.websecurity.auth.PermitAccess;
import org.eu.rainx0.raintool.core.starter.websecurity.auth.jwt.JwtAuthCheckingFilter;
import org.eu.rainx0.raintool.core.starter.websecurity.auth.sms.SmsAuthProcessingFilter;
import org.eu.rainx0.raintool.core.starter.websecurity.auth.sms.SmsAuthProvider;
import org.eu.rainx0.raintool.core.starter.websecurity.auth.username.UsernameAuthProcessingFilter;
import org.eu.rainx0.raintool.core.starter.websecurity.auth.username.UsernameAuthProvider;
import org.eu.rainx0.raintool.core.starter.websecurity.exception.handler.GlobalSecurityExceptionCatchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/7/7 17:48
 */
@Configuration
@EnableWebSecurity //  若是引入的 spring-boot-starter-security, 则可省略
// @EnableMethodSecurity
@EnableMethodSecurity(
    // 默认开启, 会激活@PreAuthorize和@PostAuthorize，这两个注解都是方法或类级别的注解
    // @PreAuthorize表示访问方法或类在执行之前先判断权限，大多情况下都是使用这个注解，注解的参数和access()方法参数值相同，都是权限表达式
    //  - 如@PreAuthorize(“hasRole(‘ROLE_abc’)”) 表示拥有abc角色可以访问，但是也可以在写成@PreAuthorize(“hasRole(‘abc’)”)
    //    (但是如果是在 configure方法中使用access表达式配置，角色前面不能以ROLE_开头)
    // @PostAuthorize 表示方法或类执行结束后判断权限，此注解很少被使用。
    prePostEnabled = true,
    // 默认关闭, 会激活@Secured，开启基于角色注解的访问控制，将注解@Secured(“ROLE_角色”) 标注在Controller的方法上，那么只有有相应角色的用户才能够访问该方法。
    securedEnabled = false
)
@Slf4j
public class WebSecurityConfig {

    public static final String[] swagger_paths = {
        // -- Swagger UI v2
        "/v2/api-docs",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**",

        // -- Swagger UI v3 (OpenAPI)
        "/v3/api-docs/**",
        "/v3/api-docs.yaml",
        "/swagger-ui/**",

        // swagger-ui shortcut path
        "/",

        // 忽略 /error 页面
        "/error",

        // others
        "/css/**", "/js/**", "/index.html", "/img/**", "/fonts/**", "/favicon.ico",
    };


    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Autowired
    private AuthSuccessHandler authSuccessHandler;

    @Autowired
    private AuthFailedHandler authFailedHandler;

    /**
     * 配置静态资源不走 filter chain
     * <p>
     * 这种方式是不走过滤器链的, 跟 Spring Security 无关. 例如, 放行前端页面的静态资源
     * 而通过 SecurityFilterChain 方式, 即使是放行, 也是走 Spring Security 过滤器链的，
     * 在过滤器链中，给请求放行, 在请求通过的时候可以获取 security 相关的数据,
     * 例如 登录接口 就应该放在此处放行 (因为在这个过程中，还有其他事情要做。)
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (WebSecurity web) -> {
            if (applicationContext instanceof ReactiveWebApplicationContext) {
                log.debug(";;[reactive] app detected");

                web.ignoring().requestMatchers(swagger_paths)
                // todo 忽略reactive application 常见的静态资源路径
                // 此处 PathRequest 需要分情况引入 (servlet, reactive 两种包)
                //    .requestMatchers(org.springframework.boot.autoconfigure.security.reactive.PathRequest.toStaticResources().atCommonLocations())
                ;

            } else {
                log.debug(";;[servlet] app detected");
                web.ignoring()
                    .requestMatchers(swagger_paths)// // 忽略 swagger 接口路径
                    // 忽略常见的静态资源路径
                    // 此处 PathRequest 导入需要分情况 (servlet, reactive 两种情况)
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                    // h2 console
                    .requestMatchers(PathRequest.toH2Console())

                ;
            }


        };
    }

    /**
     * 注册自定义 filter
     * <p>
     * 有如下几种方式:
     * - OncePerRequestFilter + @Component (+ @Order)
     * - 当前这种方式(FilterRegistrationBean)
     * - addFilterBefore(xxx, xxx)
     */
    // @Bean
    public FilterRegistrationBean<GlobalSecurityExceptionCatchFilter> filterFilterRegistrationBean() {
        FilterRegistrationBean<GlobalSecurityExceptionCatchFilter> registry = new FilterRegistrationBean<>();
        registry.setFilter(new GlobalSecurityExceptionCatchFilter(exceptionResolver));
        registry.setOrder(-101); // 最先进入, 最后出来
        // registry.setUrlPatterns();
        return registry;
    }

    public void commonSettings(HttpSecurity http) throws Exception {
        http
            // .formLogin(Customizer.withDefaults()) // 若开启, 需要这行
            .formLogin(c -> c.disable())
            .logout(c -> c.disable())
            .httpBasic(c -> c.disable())
            .csrf(c -> c.disable())
            // requestCache用于重定向，前后端分离项目无需重定向，
            .requestCache(c -> c.requestCache(new NullRequestCache()))
            .headers(c -> c
                // 禁用缓存
                .cacheControl(cc -> cc.disable())
                // 防止iframe 造成跨域, // H2 控制台等支持
                .frameOptions(cc -> cc.disable())
            )

            // security 相关异常处理
            // 默认行为是返回一个 默认 html 页面, 这里改为抛出异常
            // 这里抛出的异常可在优先级最高的 filter 中对 "filterChain.doFilter(request, response);" 进行 catch 来去全局捕获,
            // 然后 HandlerExceptionResolver.resolve(..) 到全局异常处理器中来进行处理
            .exceptionHandling(c -> c
                // 权限不足异常
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(
                        HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException
                    ) throws IOException, ServletException {
                        // 这里可以注释掉, 放在统一的 全局  exception Handler 中处理
                        // 默认情况下, 当用户在没有授权的情况下访问受保护的REST资源时，将调用此方法发送403 Forbidden响应
                        // response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException == null ? "UnAuthorized" : accessDeniedException.getMessage());
                        // ServletUtils.sendResponseContent(response, BeanUtils.toJson(ResponseWrapper.error(CodeEnum.UNAUTHORIZED_ACCESS_DENIED_ERROR)));

                        throw accessDeniedException;
                    }
                })

                // 登录认证异常
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(
                        HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException
                    ) throws IOException, ServletException {
                        // 当用户尝试访问安全的REST资源而不提供任何凭据时，将调用此方法发送401 响应
                        // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException == null ? "Unauthenticated" : authException.getMessage());
                        // ServletUtils.sendResponseContent(response, BeanUtils.toJson(ResponseWrapper.error(CodeEnum.UNAUTHENTICATED_ERROR)));

                        throw authException;
                    }
                })
            )

            .sessionManagement(c -> {

                // 不需要 session, 因为是基于 token 进行校验的
                c.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

                // 每个账号最多允许 一个登录 session
                c.maximumSessions(1).expiredSessionStrategy(new SessionInformationExpiredStrategy() {
                    // 超过数量的 session 如何处理
                    @Override
                    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
                        throw new AccountExpiredException("Your account logged in from another device");
                    }
                });

            })


            //
            // 禁用 security 内部的跨域支持, 完全交给  Spring MVC 或者前端去处理跨域
            // .cors(cusCors -> cusCors.disable())
            //
            // 启用 Spring Security 的默认 CORS 配置,
            // Spring Security 会遵循  @CrossOrigin 注解或 WebMvcConfigurer 定义的规则进行跨域控制
            // .cors(Customizer.withDefaults())
            //
            // 自定义跨域支持
            .cors(cusCors -> cusCors.configurationSource(corsConfigurationSource()))

            // 记住我
            // 用户在登录页勾选记住我之后, 登录信息被保存起来(内存/数据库), 一段时间内无需登录
            // .rememberMe(cusRememberme -> cusRememberme
            //     // 自定义登录逻辑
            //     .userDetailsService()
            //     .rememberMeParameter("hah") // 自定义表单中参数名字, 默认是remember-me
            //     // 持久层对象
            //     .tokenRepository(persistentTokenRepository())
            //     .tokenValiditySeconds(60 * 5) // 有效时间, 默认是两周时间
            // )


            /**
             * 过滤器顺序问题:
             *
             * addFilterAt(A, B.class) A 会替换掉 B
             * addFilter(Filter filter) 添加在所有默认 filter 最后,
             */
            // 添加 jwt filter
            // http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

            // 全局异常捕获, 排在最外层,
            // (或者使用 @component + @order(-101) 来保证在最外层 )
            .addFilterBefore(new GlobalSecurityExceptionCatchFilter(exceptionResolver), SecurityContextHolderFilter.class)
        ;
    }


    /**
     * 登录 api (拦截登录请求, 登录验证成功后, 在 success Handler 中返回 Token)
     * 这种方式不依赖springmvc,
     */
    @Bean
    public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {
        commonSettings(http);


        http.securityMatcher("/auth/login/*") // 使用securityMatcher限定当前配置作用的路径, 默认支持 ant pattern
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

        // username 登录方式
        UsernameAuthProcessingFilter usernameAuthFilter = new UsernameAuthProcessingFilter(
            new AntPathRequestMatcher("/auth/login/username", HttpMethod.POST.name()),
            new ProviderManager(List.of(
                SpringContextTools.getBean(UsernameAuthProvider.class)
            )),
            authSuccessHandler, authFailedHandler
        );
        http.addFilterBefore(usernameAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // sms 登录方式
        SmsAuthProcessingFilter smsAuthFilter = new SmsAuthProcessingFilter(
            new AntPathRequestMatcher("/auth/login/sms", HttpMethod.POST.name()),
            new ProviderManager(List.of(
                SpringContextTools.getBean(SmsAuthProvider.class)
            )),
            authSuccessHandler, authFailedHandler
        );
        http.addFilterBefore(smsAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /**
     * 这是兜底的 SecurityFilterChain
     * 没有指定securityMatcher表示匹配不上其他过滤链时，都走这个过滤链, 放在最后
     * <p>
     * 多个 SecurityFilterChain 如何保证加载顺序(优先级)?
     * - 根据 @Order 注解, 大的排在后面, 如 @Order(Integer.MAX_VALUE) 保证最后
     * - 或者根据 @Bean 在 @configuration class 中的定义顺序依次加载
     * (note: 指定了任意@order的SecurityFilterChain会排在未指定顺序的前面)
     */
    @Bean
    public SecurityFilterChain finalFilterChain(HttpSecurity http) throws Exception {
        commonSettings(http);
        http
            // .anonymous(c -> c.disable())
            .authorizeHttpRequests(auth -> auth
                /**
                 * anonymous() :匿名访问，仅允许匿名用户访问 (认证后反而不能访问)
                 * permitAll() 登录能访问,不登录也能访问
                 */
                .requestMatchers(pathsByAnnotation(PermitAccess.class)).permitAll()
                .requestMatchers(pathsByAnnotation(AnonymousAccess.class)).anonymous()

                /**
                 * 配置路由
                 *
                 * ant 表达式语法:
                 *      ?: 匹配某一个字符
                 *      *: 匹配0个or一个 or多个字符
                 *      **: 匹配0个or一个 or多个目录
                 *
                 *      /** — 特殊字符串，匹配所有路径
                 *      ** — 特殊字符串，匹配所有路径
                 *      /bla/**  — 匹配所有以/bla/开头的路径
                 *
                 *      最长匹配原则:
                 *      /project/upload/avatar.jpg 对于两个模式会匹配较为长/详细的那个
                 *          /project/upload/*.jpg       匹配
                 *          /** /*.jpg                  不匹配
                 *
                 *      match("/root/*", "/root/aaa/")          //false, 结束符不一致
                 *      match("/root/aaa/*", "/root/aaa/");     // true
                 *      match("/root/aaa/**", "/root/aaa/");    // true
                 */
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 自定义权限验证逻辑
                // 这里表示: 所有 /admin/** 路径都要求用户具有 ADMIN role, 其他路径默认放行
                // 配置后, 可以无需  @PreAuthorize 或 @Secured 注解了
                // .anyRequest().access((authentication, authorizationContext) -> {
                //     boolean granted = false;
                //     HttpServletRequest request = authorizationContext.getRequest();
                //     if (!request.getRequestURI().startsWith("/admin")) {
                //         granted = true;
                //     }
                //     Authentication au = authentication.get();
                //     if (au != null && au.getAuthorities().stream()
                //         .anyMatch(grant -> grant.getAuthority().equals("ADMIN"))) {
                //         granted = true;
                //     }
                //     return new AuthorizationDecision(granted);
                // })
                //

                // get white list and let go
                // .requestMatchers(getWhiteList()).permitAll()

                // 剩下的任意路由都要 auth, (shall be placed at the tail)
                .anyRequest().authenticated()
            )


            // 可选
            // 只需要自定义了 userDetailsService 并注册进了容器, 这行可以注释掉
            // 若没有注册进 spring, 则需要在这里显式指定
            // 添加自定义provider 之后，security 配置就不会自动注入 DaoAuthenticationProvider 了，如果还想使用，就调用下方代码指定自定义 userDetailsService
            // .userDetailsService(userDetailsService)
            // .authenticationProvider(jwtAuthenticationProvider)
            // .authenticationManager()

            // 将 jwt checking 作为全局登录校验 Filter
            // 也可以指定只校验部分 api
            .addFilterBefore(new JwtAuthCheckingFilter(), UsernamePasswordAuthenticationFilter.class)

        ;


        return http.build();
    }

    /**
     * use global cross replace @CrossOrigin on Controller level
     */
    private CorsConfigurationSource corsConfigurationSource() {
        if (applicationContext instanceof ReactiveWebApplicationContext) {
            throw new RuntimeException(" reactive application is not supported");
        }

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("*"));

//            configuration.setAllowedMethods(Arrays.asList("GET", "POST","PUT"));
        configuration.setAllowedMethods(Arrays.asList("*"));

        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setAllowCredentials(true); // 是否允许携带 cookie 等凭证
        configuration.setMaxAge(Duration.ofMinutes(10));// 预检请求缓存时间

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);// 所有路径都应用此规则
        return source;
    }

    /**
     * 根据注解获取请求路由
     */
    private String[] pathsByAnnotation(Class<? extends Annotation> annoType) {
        HashSet<String> result = new HashSet<>();

        // 指定名字, 否则会报错(有多个同类型的实例)
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
            .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> mappings = requestMappingHandlerMapping.getHandlerMethods();

        mappings.entrySet().forEach(entry -> {
            HandlerMethod method = entry.getValue();
            Annotation anno = method.getMethodAnnotation(annoType);

            if (anno != null) {
                // 不要用这种方式, 否则controller 类上的 path 不会被拼上来
                // PatternsRequestCondition patternsCondition = entry.getKey().getPatternsCondition();
                // if (patternsCondition != null) {
                //     result.addAll(patternsCondition.getPatterns());
                // }

                PathPatternsRequestCondition pathPatternsCondition = entry.getKey().getPathPatternsCondition();
                if (pathPatternsCondition != null) {
                    List<String> collect = pathPatternsCondition.getPatterns().stream()
                        .map(p -> p.getPatternString())
                        .collect(Collectors.toList());
                    result.addAll(collect);
                }

            }
        });

        log.debug(";;{} paths resolved: {}", annoType.getSimpleName(), result);

        return result.toArray(new String[0]);
    }

    /**
     * 基于默认内存模型的用户模式
     */
    // @Bean
    public UserDetailsService userDetailsServiceInMemory() {
        UserDetails userDetails = User.builder().username("rain")
            // .password("{noop}rain") // 密码明文, 无需 password encoder
            .password("rain").passwordEncoder(pwd -> passwordEncoder().encode(pwd))
            .roles("USER")
            .authorities("ROLE_USER").build();

        /**
         * 在初始化的时候，创建了一个HashMap，所有的用户信息以username作为“主键”都放在了HashMap中
         *         createUser(UserDetails user)
         *         updateUser(UserDetails user)
         *         deleteUser(String username)
         *         changePassword(String oldPassword, String newPassword)
         *         userExists(String username)
         */
        return new InMemoryUserDetailsManager(userDetails);
    }


//    //基于默认数据库模型的用户模式
//    @Bean
//    public UserDetailsService jdbcUserDetailsService(DataSource dataSource){
//        // 基于 jdbctemplate orm, 若是用mybatis/ jpa, 则不能用
//        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
//
//        if(!manager.userExists("admin")){
//            //下面的创建用户就相当于通过 jdbc 调用 mysql 的 insert
//            manager.createUser(User.withUsername("admin")
//                .password("{noop}xxx")
//                .roles("ADMIN")
//                .build());
//        }
//
//        return  manager;
//    }

    /**
     * SHA-256+随机盐+密钥 对密码进行加密
     * <p>
     * encode() 通过 hash 进行加密 不可逆
     * matches(原始明文{即需要验证的密码}, 密文{一般来自数据库})
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        // 4 ~ 31
        return new BCryptPasswordEncoder(4);
    }

    /**
     * Shall call authenticationManager.authenticate() when login
     */
    // @Bean
    @ConditionalOnMissingBean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
