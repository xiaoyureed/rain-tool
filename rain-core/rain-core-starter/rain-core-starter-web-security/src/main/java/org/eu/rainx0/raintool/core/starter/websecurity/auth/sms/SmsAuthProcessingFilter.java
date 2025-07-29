package org.eu.rainx0.raintool.core.starter.websecurity.auth.sms;

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
 * @author xiaoyu
 * @time 2025/7/12 09:51
 */
public class SmsAuthProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public SmsAuthProcessingFilter(
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
        String phone = params.get("phone").toString();
        String smsCode = params.get("smsCode").toString();

        if (StringUtils.isBlank(phone) || StringUtils.isBlank(smsCode)) {
            throw new AuthenticationServiceException("参数缺失");
        }


        SmsAuthToken auth = new SmsAuthToken()
            .setPhone(phone)
            .setSmsCode(smsCode);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return getAuthenticationManager().authenticate(auth);
    }
}
