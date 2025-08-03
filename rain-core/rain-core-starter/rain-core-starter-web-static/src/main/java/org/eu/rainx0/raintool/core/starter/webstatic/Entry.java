package org.eu.rainx0.raintool.core.starter.webstatic;

import org.eu.rainx0.raintool.core.starter.webstatic.config.WebStaticProps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 * tips:
 *  - frontend 中 package.json 修改 "homepage": "./"
 *  - 拷贝 build/ 到 /resource/static 下
 *
 * @author: xiaoyu
 * @time: 2025/7/1 13:36
 */
@Slf4j
@ComponentScan
@EnableConfigurationProperties({WebStaticProps.class})
public class Entry {
    {
        log.debug(";;Web static starter loaded");
    }
}
