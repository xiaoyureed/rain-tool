package org.eu.rainx0.raintool.core.starter.web.filter;

import org.apache.commons.lang3.StringUtils;
import org.eu.rainx0.raintool.core.common.Consts;
import org.eu.rainx0.raintool.core.common.context.LoginContext;
import org.eu.rainx0.raintool.core.common.exception.BizException;
import org.eu.rainx0.raintool.core.common.util.JwtTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 18:27
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Value("${rain.web.auth.enabled:false}")
    private Boolean authEnabled;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!authEnabled) {
            return true;
        }

        String token = request.getHeader(Consts.Web.Headers.AUTH);
        if (StringUtils.isBlank(token)) {
            throw new BizException("token missing");
            // return false;
        }
        if (!token.startsWith("Bearer ")) {
            throw new BizException("Unsupported auth mode");
        }

        token = token.substring(7);
        Claims claims = JwtTools.parseToken(token);

        Integer id = claims.get("id", Integer.class);
        //todo
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginContext.clear();
    }


}
