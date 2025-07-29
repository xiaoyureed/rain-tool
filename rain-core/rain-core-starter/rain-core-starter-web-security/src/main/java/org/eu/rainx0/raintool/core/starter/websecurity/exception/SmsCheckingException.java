package org.eu.rainx0.raintool.core.starter.websecurity.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author xiaoyu
 * @time 2025/7/12 10:28
 */
public class SmsCheckingException extends AuthenticationException {
    public SmsCheckingException() {
        super("Sms checking not pass");
    }
}