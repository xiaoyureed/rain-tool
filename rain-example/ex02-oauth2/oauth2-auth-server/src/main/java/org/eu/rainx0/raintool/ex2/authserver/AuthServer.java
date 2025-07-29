package org.eu.rainx0.raintool.ex2.authserver;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/**
 * @author xiaoyu
 * @time 2025/7/15 10:25
 */
@SpringBootApplication
@EnableWebSecurity
public class AuthServer {
    public static void main(String[] args) {
        SpringApplication.run(AuthServer.class, args);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("sa")
            .password("{noop}sa")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            // 在 auth server 中可开可不开, 一般是放在 resource server 中开
            // 最好打开, 会引入一些自动配置, 删掉的话 client app 无法启动
            .oauth2ResourceServer(c -> c
                .jwt(Customizer.withDefaults())
            )
        ;

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("hi")
            .clientSecret("{noop}123")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://localhost:9091/login/oauth2/code/hi_registration")
            // .scope()
            .scopes(s -> {
                s.add("read");
                s.add("write");
            })
            .clientSettings(ClientSettings.builder()
                .requireAuthorizationConsent(true)
                .requireProofKey(false)
                .build()
            )
            .build();
        return new InMemoryRegisteredClientRepository(client);
    }

    /**
     *
     * 功能	Endpoint	HTTP 方法	描述
     * 认证授权	/oauth2/authorize	GET	发起授权请求（支持授权码、implicit 等）
     * 获取 Token	/oauth2/token	POST	客户端用授权码等换取访问令牌（access token）
     * 令牌校验	/oauth2/introspect	POST	检查 access token 是否有效
     * 令牌吊销	/oauth2/revoke	POST	主动撤销 access/refresh token
     * JWKS 公开密钥	/oauth2/jwks	GET	提供 token 签名公钥（JWT 签名验证）
     * OIDC 配置（如启用）	/.well-known/openid-configuration	GET	OpenID Connect metadata 信息
     * 用户信息（如启用）	/userinfo	GET/POST	返回当前授权用户的基本信息（需配置支持）功能	Endpoint	HTTP 方法	描述
     * 认证授权	/oauth2/authorize	GET	发起授权请求（支持授权码、implicit 等）
     * 获取 Token	/oauth2/token	POST	客户端用授权码等换取访问令牌（access token）
     * 令牌校验	/oauth2/introspect	POST	检查 access token 是否有效
     * 令牌吊销	/oauth2/revoke	POST	主动撤销 access/refresh token
     * JWKS 公开密钥	/oauth2/jwks	GET	提供 token 签名公钥（JWT 签名验证）
     * OIDC 配置（如启用）	/.well-known/openid-configuration	GET	OpenID Connect metadata 信息
     * 用户信息（如启用）	/userinfo	GET/POST	返回当前授权用户的基本信息（需配置支持）
     */
    // @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
            .authorizationEndpoint("/auth/authorize")
            .tokenEndpoint("/auth/token")
            .jwkSetEndpoint("/auth/jwks")
            .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }


    private static RSAKey generateRsa() {
        KeyPair keyPair = generateRsaKey();
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
            .privateKey((RSAPrivateKey) keyPair.getPrivate())
            .keyID(UUID.randomUUID().toString())
            .build();
    }

    static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
