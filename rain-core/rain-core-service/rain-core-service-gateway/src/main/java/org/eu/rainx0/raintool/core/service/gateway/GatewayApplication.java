package org.eu.rainx0.raintool.core.service.gateway;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;

import reactor.core.publisher.Mono;

/**
 * @author xiaoyu
 * @time 2025/7/1 17:02
 */
@SpringBootApplication
// Enable service discovery functionality,
// We already configured it in starter, so here we can omit it
// @EnableDiscoveryClient
@RestController
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @GetMapping("hello")
    public Mono<String> hello() {
        return Mono.just("hello from gateway");
    }

    @GetMapping("/gw/flow/rules")
    public Set<GatewayFlowRule> currentGatewayFlowRules() {
        return GatewayRuleManager.getRules();
    }

    @GetMapping("/gw/api/groups")
    public Set<ApiDefinition> apiGroups() {
        return GatewayApiDefinitionManager.getApiDefinitions();
    }


    /**
     * 自动监听配置文件变更
     */
    // @Bean
    public ApplicationRunner init(NacosConfigManager nacosConfigManager) {
        return args -> {
            // nacosConfigManager.getConfigService().publishConfig("demo-gateway.yaml", "DEFAULT_GROUP", "");
            ConfigService configService = nacosConfigManager.getConfigService();
            configService.addListener("demo-gateway.yaml", "DEFAULT_GROUP", new Listener() {
                // 执行监听任务的 thread pool
                @Override
                public Executor getExecutor() {
                    return Executors.newFixedThreadPool(4);
                }

                @Override
                public void receiveConfigInfo(String s) {

                    System.out.println("变化后的配置: " + s);
                }
            });
        };
    }
}