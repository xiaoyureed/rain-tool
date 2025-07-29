package org.eu.rainx0.raintool.example.security.simple;

import org.eu.rainx0.raintool.core.starter.websecurity.exception.SmsCheckingException;
import org.eu.rainx0.raintool.core.starter.websecurity.exception.UserPhoneNotFoundException;
import org.eu.rainx0.raintool.core.starter.websecurity.auth.sms.UserDetailsServiceSms;
import org.eu.rainx0.raintool.example.security.simple.entity.Account;
import org.eu.rainx0.raintool.example.security.simple.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/7/12 10:32
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService, UserDetailsServiceSms {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByPhone(String phone) throws UserPhoneNotFoundException {
        Account account = accountRepository.findAccountByPhone(phone).orElseThrow(UserPhoneNotFoundException::new);
        return buildUser(account);
    }

    @Override
    public void smsChecking(String phone, String smsCode) throws SmsCheckingException {
        //todo
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));

        //登录时请求中的密码和这里根据 username查询到的密码进行比对
        return buildUser(account);
    }

    private UserDetails buildUser(Account account) {
        return User.builder()
            .username(account.getUsername())
            .password(account.getPassword())
            .accountLocked(!account.getEnabled())
            .accountExpired(false)
            .credentialsExpired(false)
            .roles("USER")
            .authorities("ROLE_USER")
            .build();
    }
}
