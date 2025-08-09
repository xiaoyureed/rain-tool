package org.eu.rainx0.raintool.core.starter.netty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author xiaoyu
 * @time 2025/8/7 21:20
 */
@ComponentScan
@Slf4j
@EnableAsync
public class Entry {
    {
        log.debug(";;Netty starter loaded.");
    }
}
