package org.eu.rainx0.raintool.core.starter.webstatic;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/xiaoyureed/next-boot
 *
 * @author: xiaoyu
 * @time: 2025/7/1 13:36
 */
@Slf4j
@ComponentScan
@EnableConfigurationProperties({WebStaticProperties.class})
public class StarterConfig {
    {
        log.debug(";;Web static starter loaded");
    }
}
