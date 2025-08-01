package org.eu.rainx0.raintool.core.starter.tx;

import org.springframework.context.annotation.ComponentScan;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaoyu
 * @time 2025/7/31 10:46
 */
@Slf4j
@ComponentScan
public class Entry {
    @PostConstruct
    void init() {
        log.debug(";; Tx starter loaded.");
    }
}
