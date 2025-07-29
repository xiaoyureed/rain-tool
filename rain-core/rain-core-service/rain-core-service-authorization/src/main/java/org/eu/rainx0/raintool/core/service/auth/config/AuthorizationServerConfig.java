package org.eu.rainx0.raintool.core.service.auth.config;

import java.time.Duration;

import org.eu.rainx0.raintool.core.common.util.RandomTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author: xiaoyu
 * @time: 2025/7/7 17:17
 */
@Configuration
// 引入默认的 out-of-the-box 的授权服务器端点, 若想自定义, 需要手动配置 SecurityFilterChain bean
// @Import(OAuth2AuthorizationServerConfiguration.class)
public class AuthorizationServerConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
        return httpSecurity.build();
    }


    /**
     * register the third party client app
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient client = RegisteredClient.withId(RandomTools.uuid32())
            .clientId("hello-api") // 第三方 client id
            .clientSecret("{noop}hello-secret")  // 第三方 client secret
            .scope("all")  // 授权范围
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(1))
                .refreshTokenTimeToLive(Duration.ofHours(7))
                .build()
            )
            .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
            .build();
        return new InMemoryRegisteredClientRepository(client);
    }


    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.builder()
            .username("sa")
            .password("{noop}sa")
            .authorities("ROLE_ADMIN")
            .build();
        return new InMemoryUserDetailsManager(userDetails);
    }


}
