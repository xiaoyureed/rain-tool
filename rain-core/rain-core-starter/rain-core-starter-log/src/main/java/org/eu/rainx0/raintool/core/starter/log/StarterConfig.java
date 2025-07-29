package org.eu.rainx0.raintool.core.starter.log;

import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/6/29 17:35
 */
@Slf4j
@ComponentScan // This annotation here is used to scan the other beans
public class StarterConfig {
    {
        log.debug(";;Log starter loaded.");
    }
}
