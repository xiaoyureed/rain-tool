package org.eu.rainx0.raintool.core.starter.websecurity.auth.username;

import org.eu.rainx0.raintool.core.common.model.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 接收 unauthed Authentication 对象, 进行认证
 *
 * @author xiaoyu
 * @time 2025/7/11 14:21
 */
@Component
public class UsernameAuthProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String username = (String) auth.getPrincipal();
        String password = (String) auth.getCredentials();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }

        UsernameAuthToken ret = new UsernameAuthToken(null);
        ret.setUsername(username);
        ret.setPassword(password);
        ret.setDetails(auth.getDetails());
        ret.setLoginInfo(new LoginInfo().setUsername(username));
        ret.setAuthenticated(true);

        return ret;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        // authentication 是父类/相同类
        // (authentication 对应类型可以接受 UsernameAuthToken 类型的变量)
        return authentication.isAssignableFrom(UsernameAuthToken.class);
    }
}
