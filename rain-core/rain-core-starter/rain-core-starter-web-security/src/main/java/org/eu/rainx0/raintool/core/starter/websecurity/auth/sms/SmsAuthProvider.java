package org.eu.rainx0.raintool.core.starter.websecurity.auth.sms;

import org.eu.rainx0.raintool.core.common.model.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/7/12 09:52
 */
@Component
public class SmsAuthProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(userDetailsService instanceof UserDetailsServiceSms)) {
            throw new AuthenticationServiceException(UserDetailsServiceSms.class.getName() + " is not found");
        }
        UserDetailsServiceSms service = (UserDetailsServiceSms) userDetailsService;
        String phone = authentication.getPrincipal().toString();
        String smsCode = authentication.getCredentials().toString();

        service.smsChecking(phone, smsCode);
        UserDetails userDetails = service.loadUserByPhone(phone);

        SmsAuthToken ret = new SmsAuthToken();
        ret.setSmsCode(smsCode);
        ret.setPhone(phone);
        ret.setDetails(authentication.getDetails());
        ret.setAuthenticated(true);
        ret.setLoginInfo(new LoginInfo().setUsername(userDetails.getUsername()));

        return ret;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthToken.class.isAssignableFrom(authentication);
    }
}
