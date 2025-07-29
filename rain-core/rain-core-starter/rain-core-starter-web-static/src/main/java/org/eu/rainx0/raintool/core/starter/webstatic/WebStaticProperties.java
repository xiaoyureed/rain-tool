package org.eu.rainx0.raintool.core.starter.webstatic;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 13:37
 */
@Data
@ConfigurationProperties("core.starter.webstatic")
public class WebStaticProperties {
    private Boolean enabled = true;
}