package org.eu.rainx0.raintool.ex2.oauth2server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.web.FilterChainProxy;

/**
 * @author xiaoyu
 * @time 2025/7/14 16:15
 */
@SpringBootApplication
public class Oauth2AuthServer {
    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(Oauth2AuthServer.class, args);
        FilterChainProxy filterChainProxy = app.getBean(FilterChainProxy.class);
        System.out.println(filterChainProxy);
    }
}
