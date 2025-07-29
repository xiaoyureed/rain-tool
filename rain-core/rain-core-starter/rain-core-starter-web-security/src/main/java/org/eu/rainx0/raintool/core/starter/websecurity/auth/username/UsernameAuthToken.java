package org.eu.rainx0.raintool.core.starter.websecurity.auth.username;

import java.util.Collection;

import org.eu.rainx0.raintool.core.common.model.LoginInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

/**
 * username auth token
 *  必须是 {@link Authentication} 实现类, AbstractAuthenticationToken 提供了一些默认实现
 *
 * @author: xiaoyu
 * @time: 2025/7/11 11:42
 */
@Getter
@Setter
public class UsernameAuthToken extends AbstractAuthenticationToken {

    private String username; // 前端传过来
    private String password; // 前端传过来
    private LoginInfo loginInfo; // 认证成功后，后台从数据库获取信息

    public UsernameAuthToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        // 授权成后，Credential（比如，登录密码）信息最好清空
        if (isAuthenticated()) {
            return null;
        }
        return this.password;
    }

    @Override
    public Object getPrincipal() {
        // 根据SpringSecurity的设计，授权成功之前，getPrincipal返回的客户端传过来的数据。
        // 授权成功后，返回当前登陆用户的信息
        if (isAuthenticated()) {
            return this.loginInfo;
        }
        return this.username;
    }
}
