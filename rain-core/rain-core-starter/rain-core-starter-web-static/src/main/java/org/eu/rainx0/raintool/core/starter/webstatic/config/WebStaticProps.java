package org.eu.rainx0.raintool.core.starter.webstatic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 13:37
 */
@Data
@ConfigurationProperties("rain.web-static")
public class WebStaticProps {
    private boolean enabled = true;
    /**
     * 不存在后端逻辑, 如果是 true, 所有请求会直接到 /static, 否则会经过 springmvc 进行路由
     */
    private boolean noBackendLogic = false;
}