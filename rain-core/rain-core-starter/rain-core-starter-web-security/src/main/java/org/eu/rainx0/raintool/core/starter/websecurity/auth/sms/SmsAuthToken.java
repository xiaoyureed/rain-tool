package org.eu.rainx0.raintool.core.starter.websecurity.auth.sms;

import org.eu.rainx0.raintool.core.common.model.LoginInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xiaoyu
 * @time 2025/7/12 09:52
 */
@Getter
@Setter
public class SmsAuthToken extends AbstractAuthenticationToken {
    private String phone;
    private String smsCode;
    private LoginInfo loginInfo;

    public SmsAuthToken() {
        super(null);
    }

    @Override
    public Object getCredentials() {
        return isAuthenticated() ? null : smsCode;
    }

    @Override
    public Object getPrincipal() {
        return isAuthenticated() ? loginInfo : phone;
    }
}
