package org.eu.rainx0.raintool.example.security.simple.controller;

import java.util.Collection;
import java.util.List;

import org.eu.rainx0.raintool.core.common.model.ResponseWrapper;
import org.eu.rainx0.raintool.example.security.simple.entity.Account;
import org.eu.rainx0.raintool.example.security.simple.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;


/**
 * @author xiaoyu
 * @time 2025/7/9 23:28
 */
@RestController
@Slf4j
public class AccountController {

    @Resource
    private AccountRepository accountRepository;

    @GetMapping("/accounts")
    public ResponseEntity<ResponseWrapper<List<Account>>> accounts() {
        List<Account> all = accountRepository.findAll();
        return ResponseEntity.ok(ResponseWrapper.ok(all));
    }

    private void printSecurityInfo() {
        // this is UsernamePasswordAuthenticationToken
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("auth: {}", auth);

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        log.info("authorities: {}", authorities); // [ROLE_USER]

        User principal = (User) auth.getPrincipal();
        log.info("principal: {}, password: {}", principal, principal.getPassword());// password = null (进行了 脱敏处理)

        Object credentials = auth.getCredentials();
        log.info("credentials: {}", credentials); // null

        WebAuthenticationDetails details = (WebAuthenticationDetails) auth.getDetails();
        log.info("details: {}", details); // [RemoteIpAddress=0:0:0:0:0:0:0:1, SessionId=08552ABAFB8081F62216C17CA2DECFA0]

    }
}
