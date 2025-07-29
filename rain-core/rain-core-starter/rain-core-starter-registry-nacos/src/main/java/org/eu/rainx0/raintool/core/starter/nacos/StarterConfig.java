package org.eu.rainx0.raintool.core.starter.nacos;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 11:32
 */
@Slf4j
@ComponentScan
//@EnableNacosDiscovery(globalProperties = @NacosProperties(serverAddr = "nacos的ip地址:nacos的端口"))
@EnableDiscoveryClient
public class StarterConfig {
    {
        log.debug(";;Nacos starter loaded");
    }
}
