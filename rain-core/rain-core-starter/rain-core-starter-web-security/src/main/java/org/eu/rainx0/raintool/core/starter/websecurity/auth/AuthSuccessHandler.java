package org.eu.rainx0.raintool.core.starter.websecurity.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.eu.rainx0.raintool.core.common.model.LoginInfo;
import org.eu.rainx0.raintool.core.common.model.ResponseWrapper;
import org.eu.rainx0.raintool.core.common.util.JwtTools;
import org.eu.rainx0.raintool.core.starter.util.BeanTools;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 登录成功处理, return access token and refresh token back
 *
 * @author: xiaoyu
 * @time: 2025/7/11 12:42
 */
@Component
public class AuthSuccessHandler
    // 用于设置 禁用redirect 策略
    extends AbstractAuthenticationTargetUrlRequestHandler
    implements AuthenticationSuccessHandler {

    public AuthSuccessHandler() {
        this.setRedirectStrategy(new RedirectStrategy() {
            @Override
            public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
                // Do nothing, no redirects in REST
            }
        });
    }


    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request, HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        Object principalRaw = authentication.getPrincipal();

        if (!(principalRaw instanceof LoginInfo)) {
            throw new AuthenticationServiceException("After login, the principal should be LoginInfo");
        }

        LoginInfo loginInfo = (LoginInfo) principalRaw;

        HashMap<String, Object> data = new HashMap<>(2);
        String[] token = JwtTools.generateAccessToken(loginInfo.getUsername());
        data.put("accessToken", token[0]);
        data.put("refreshToken", token[1]);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(BeanTools.toJson(ResponseWrapper.ok(data)));
        writer.flush();
        writer.close();
    }
}
