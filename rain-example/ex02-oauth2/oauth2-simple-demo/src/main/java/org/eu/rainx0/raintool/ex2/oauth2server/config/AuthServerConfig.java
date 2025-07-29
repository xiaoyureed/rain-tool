package org.eu.rainx0.raintool.ex2.oauth2server.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
import org.springframework.security.oauth2.core.oidc.OidcScopes;
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
 * // 启动应用后，授权端点默认如下：
 * // 授权端点:      /oauth2/authorize
 * // Token 端点:    /oauth2/token
 * // JWK Set:       /oauth2/jwks
 * // 取消授权:      /oauth2/revoke
 * // 登录页面:      /login
 * <p>
 * /oauth2/introspect
 * /oauth2/device_authorization
 * /oauth2/device_verification
 * /.well-known/openid-configuration	OIDC 配置
 * /.well-known/jwks.json	公钥 JWKS
 *
 * @author xiaoyu
 * @time 2025/7/14 16:16
 */
@Configuration
@EnableWebSecurity
public class AuthServerConfig {

    public static String redirect_path = "/authorized";

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("sa")
            .password("{noop}sa")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // 保证在最前面
    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {
        // 暴露默认提供的认证端点
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.formLogin(Customizer.withDefaults()); // It's important, or can't redirect to /login page

        return http.build();
    }

    /**
     * 自定义 auth endpoints, 覆盖默认的
     */
    // @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
            .authorizationEndpoint("/auth/authorize")
            .tokenEndpoint("/auth/token")
            .jwkSetEndpoint("/auth/jwks")
            // .issuer("http://localhost:" + port) // auth server address, can be omitted
            .build();
    }

    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // .requestMatchers("/authorized").permitAll() // 可省略
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(c -> c
                .jwt(Customizer.withDefaults())
            )
            ;

        return http.build();
    }

    @Value("${server.port:8080}")
    private int port;

    /**
     * 对于 authentication code 授权模式
     * 1. 从 /oauth2/authorize 获取授权码,
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("hi")
            .clientSecret("{noop}123")

            // 客户端认证方式: (即访问获取 Token 的端点的方式)
            // - CLIENT_SECRET_BASIC 客户端 ID 和客户端密钥通过 Base64 编码后放在请求头中传输
            //      示例：Authorization: Basic base64encode("client:secret")
            // - CLIENT_SECRET_POST  客户端 ID 和客户端密钥作为请求体的一部分发送
            // - none  不需要客户端认证
            // - PRIVATE_KEY_JWT 或 CLIENT_SECRET_JWT  使用 JWT 签名方式进行客户端认证
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)

            // 授权模式:
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // 授权码模式
            // .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            // .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)

            // 回调地址 (会附带授权码 code 作为参数 )
            // 可指定多个, 浏览器在认证时可手动指定redirect_uri 参数, server 端会校验是否为这里其中一个
            //      若浏览器不指定, 默认从这里选中第一个
            // 不配置会 error
            .redirectUri("http://localhost:" + port + "/authorized")


            // 授权范围 (定义 client 认证时可以申请哪些授权)
            //  Client 在请求 授权码时通过 scope 参数指定
            // .scope(OidcScopes.OPENID)
            .scope("read")

            .clientSettings(ClientSettings.builder()
                // 是否在用户授权时显示“同意授权”页面, 默认 true
                .requireAuthorizationConsent(true)
                .requireProofKey(false) // 测试期间临时禁用 PKCE（Proof Key for Code Exchange）, 默认不禁用
                .build()
            )

            .build();

        InMemoryRegisteredClientRepository ret = new InMemoryRegisteredClientRepository(registeredClient);

        return ret;
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
