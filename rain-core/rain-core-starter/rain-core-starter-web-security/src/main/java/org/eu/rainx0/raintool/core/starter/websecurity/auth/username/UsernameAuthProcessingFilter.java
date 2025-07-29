package org.eu.rainx0.raintool.core.starter.websecurity.auth.username;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eu.rainx0.raintool.core.starter.util.BeanTools;
import org.eu.rainx0.raintool.core.starter.web.util.ServletTools;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 在这个 Filter 中做 "认证" 工作:
 * - 先封装未认证的 Authentication 对象, 指定 认证成功和失败的 Handler
 * - 再 call AuthenticationManager.authenticate(auth) 执行认证
 * @author: xiaoyu
 * @time: 2025/7/11 11:52
 * https://www.bilibili.com/video/BV1Ux4y1E7kV?
 */
// @Component // 一旦注册到 Spring 容器中, 会自动加入到 Spring Security 中
public class UsernameAuthProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public UsernameAuthProcessingFilter(
        RequestMatcher matcher, AuthenticationManager manager,
        AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler
    ) {
        super(matcher, manager);
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String postBody = ServletTools.getPostBody();
        Map<String, Object> params = BeanTools.toMap(postBody);
        String username = (String) params.get("username");
        String password = (String) params.get("password");

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new AuthenticationServiceException("参数缺失");
        }

        UsernameAuthToken auth = new UsernameAuthToken(null);
        auth.setUsername(username);
        auth.setPassword(password);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        auth.setAuthenticated(false);// 可省略, 默认就是未认证的

        return getAuthenticationManager().authenticate(auth); // 调用 provider 的逻辑进行认证
    }
}
