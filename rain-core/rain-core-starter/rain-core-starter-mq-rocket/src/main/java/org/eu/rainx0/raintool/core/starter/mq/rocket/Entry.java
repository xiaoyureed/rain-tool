package org.eu.rainx0.raintool.core.starter.mq.rocket;

import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaoyu
 * @time 2025/7/17 11:53
 */
@ComponentScan
@Slf4j
public class Entry {
    {
        log.debug(";;RocketMQ starter loaded!");
    }
}
