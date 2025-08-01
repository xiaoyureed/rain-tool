package org.eu.rainx0.raintool.core.starter.mq.mqtt.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author xiaoyu
 * @time 2025/8/1 16:53
 */
@Data
@Component
@ConfigurationProperties(prefix = "rain.mqtt")
public class MqttProps {

    private String username;
    private String password;
    private String brokerUrl;
    private String[] subTopics;
    private String[] pubTopics;
    private String clientId;

    @Autowired
    private Environment env;

    @PostConstruct
    void init() {
        if (!StringUtils.hasText(clientId)) {
            String appName = env.getProperty("spring.application.name");
            if (!StringUtils.hasText(appName)) {
                throw new RuntimeException("Application name and clientId are both null.");
            }
            clientId = appName + "-client";
        }
    }
}
