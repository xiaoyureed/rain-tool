package org.eu.rainx0.raintool.core.starter.websecurity.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author xiaoyu
 * @time 2025/7/12 10:28
 */
public class UserPhoneNotFoundException extends AuthenticationException {
    public UserPhoneNotFoundException() {
        super("User phone not found");
    }
}
