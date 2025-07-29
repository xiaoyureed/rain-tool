package org.eu.rainx0.raintool.core.starter.job;

import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 14:28
 */
@Slf4j
@ComponentScan
public class StarterConfig {
    {
        log.debug(";;Job starter loaded");
    }
}
