package org.eu.rainx0.raintool.core.starter.websecurity.auth.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eu.rainx0.raintool.core.common.model.LoginInfo;
import org.eu.rainx0.raintool.core.common.util.JwtTools;
import org.eu.rainx0.raintool.core.starter.web.util.ServletTools;
import org.eu.rainx0.raintool.core.starter.websecurity.WebSecurityConfig;
import org.eu.rainx0.raintool.core.starter.websecurity.auth.username.UsernameAuthToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * This filter is a special one, meaning to checking token and fill the security context
 *
 * @author xiaoyu
 * @time 2025/7/7 22:02
 */
// 注册成为 bean 会自动加入 security Filter Chain 中, 最好手动加入, 方便指定顺序
// @Component
@Slf4j
public class JwtAuthCheckingFilter extends OncePerRequestFilter {

    private final Set<String> whiteList = new HashSet<>();

    {
        whiteList.addAll(Arrays.asList(WebSecurityConfig.swagger_paths));
        whiteList.add("/auth/**");
    }

    /**
     * 白名单中的 path 不过滤, let go
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return whiteList.stream().anyMatch(p -> new AntPathRequestMatcher(p).matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = ServletTools.getAuthenticationToken()
            .orElseThrow(() -> new AuthenticationServiceException("No valid jwt token found"));

        // 填充 security context (填充后, 后续的内置 filter 就可以通过了)
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            Claims claims;
            try {
                claims = JwtTools.parseToken(token); // jwt的生成secret 可以保证内容无法伪造, Token尾部的签名保证内容无法篡改
            } catch (Exception e) {
                throw new AuthenticationServiceException("Error when authentication", e);
            }

            String username = claims.getSubject();
            String sessionId = claims.get("sessionId", String.class);

            UsernameAuthToken auth = new UsernameAuthToken(null);
            auth.setUsername(username);
            auth.setLoginInfo(new LoginInfo().setUsername(username).setSessionId(sessionId));
            auth.setAuthenticated(true);
            // 添加一些审计信息如客户端 IP、Session ID 等信息
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
