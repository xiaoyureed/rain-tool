package org.eu.rainx0.raintool.core.starter.feign;

import java.util.List;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import feign.Logger;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用@FeignClient(
 *
 *   value/name = "xxx-service-name",
 *   path = "/api/users"  # 公共路由前缀
 *   url = "http://192.168xxx:xx"  # 直接指定地址, 优先级高于service name
 *   fallback = "XxxServiceFallback",
 *   contextId = "xxxService" # feign client 的名称，默认是 service name (配置文件中配置 timeout 会用到)
 * )
 *
 *
 * @author: xiaoyu
 * @time: 2025/6/29 17:35
 */
@Slf4j
@ComponentScan
//自动扫描 @FeignClient 注解的接口
// - basePackages：指定扫描 @FeignClient 接口的包路径
//- defaultConfiguration：指定全局的 FeignClient 配置类 （如编码器、解码器等)
@EnableFeignClients
public class Entry {
    {
        log.debug(";;Feign starter loaded.");
    }

    /**
     * 自定义日志打印
     */
    @Bean
    public Logger feignLogger() {
        // 拦截敏感信息
        final List<String> SENSITIVE_KEYS = List.of("password", "token", "secret");

        return new Logger() {
            @Override
            protected void log(String configKey, String format, Object... args) {
                String message = String.format(format, args);

                for (String key : SENSITIVE_KEYS) {
                    message = message.replaceAll("(?i)(" + key + "=)[^&\\s]+", "$1******");
                }

                log.info("[Feign] [{}] {}", configKey, message);


            }
        };
    }

    /**
     * 日志打印细致程度
     * @return
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 自定义重试机制
     */
    // @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default();
    }
}
