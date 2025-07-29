package org.eu.rainx0.raintool.ex2.authclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author xiaoyu
 * @time 2025/7/15 10:49
 */
@SpringBootApplication
public class ClientApp {
    public static void main(String[] args) {
        SpringApplication.run(ClientApp.class, args);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(c -> c
                .anyRequest().authenticated()
            )

             //启用 OAuth2 登录流程, redirect to auth server /oauth2/authorize
            .oauth2Login(Customizer.withDefaults())
            // .oauth2Login(c -> c
            //     .successHandler((request, response, authentication) -> {
            //         System.out.println("login ok: " + authentication.getPrincipal());
            //         // 重定向到源url
            //         response.sendRedirect(request.getHeader("referer"));
            //     })
            //     .failureHandler((request, response, exception) -> {
            //         exception.printStackTrace();
            //         response.sendRedirect("/login?failure");
            //     })
            // )
            ;
        return http.build();
    }

    @Bean
    WebClient webClient(ClientRegistrationRepository clientRegistrationRepository,
                        OAuth2AuthorizedClientRepository authorizedClientRepository) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
            new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
                authorizedClientRepository);
        oauth2.setDefaultOAuth2AuthorizedClient(true);
        return WebClient.builder()
            .apply(oauth2.oauth2Configuration())
            .build();
    }

}
