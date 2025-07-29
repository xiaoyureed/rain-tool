package org.eu.rainx0.raintool.core.starter.websecurity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/7/10 16:02
 */
@Data
@Component
@ConfigurationProperties(prefix = "rain.security")
public class WebSecurityProps {

    private boolean rest;
}
