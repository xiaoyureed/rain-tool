package org.eu.rainx0.raintool.core.starter.websecurity.auth.sms;

import org.eu.rainx0.raintool.core.starter.websecurity.exception.SmsCheckingException;
import org.eu.rainx0.raintool.core.starter.websecurity.exception.UserPhoneNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author xiaoyu
 * @time 2025/7/12 10:03
 */
public interface UserDetailsServiceSms {
    UserDetails loadUserByPhone(String phone) throws UserPhoneNotFoundException;

    void smsChecking(String phone, String smsCode) throws SmsCheckingException;
}

