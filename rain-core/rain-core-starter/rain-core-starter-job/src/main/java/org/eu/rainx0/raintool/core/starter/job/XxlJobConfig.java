package org.eu.rainx0.raintool.core.starter.job;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 14:30
 */
@Data
@Slf4j
@Configuration
//@EnableConfigurationProperties({
//    JobConfiguration.class
//})
@ConfigurationProperties(prefix = "raincloud.job")
public class XxlJobConfig {
    private String adminAddresses;

    private String accessToken;

    private String appName;

    private String executorAddress;

    private String executorIp;

    private Integer executorPort;

    private String logPath;

    private Integer logRetentionDays;

    @Autowired
    private Environment environment;


    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        if (executorPort == null) {
            log.warn("!!! executor port of xxlJob is not set up, will take use of the default strategy to resolve: server port + 100");

            // 未配置监听端口，则在服务启动端口上增加100
            String port = environment.getProperty("server.port");
            if (StringUtils.isBlank(port)) {
                log.warn("!!! the application port is not set up, will take use of the default: 8080");
                port = "8080";
            }

            executorPort = Integer.parseInt(port) + 100;
        }

        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(adminAddresses);
        executor.setAppname(appName);
        executor.setAddress(executorAddress);
        executor.setIp(executorIp);
        executor.setPort(executorPort);
        executor.setAccessToken(accessToken);
        executor.setLogPath(logPath);
        executor.setLogRetentionDays(logRetentionDays);

        return executor;
    }
}
