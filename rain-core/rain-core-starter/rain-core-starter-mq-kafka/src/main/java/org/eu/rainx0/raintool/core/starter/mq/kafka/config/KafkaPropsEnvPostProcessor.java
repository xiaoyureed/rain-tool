package org.eu.rainx0.raintool.core.starter.mq.kafka.config;

import java.util.Map;

import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

// @Component
@Slf4j
public class KafkaPropsEnvPostProcessor implements EnvironmentPostProcessor {

    private static final String CONFIG_SOURCE_NAME = "application.yml";

    @Override
    public void postProcessEnvironment(
        ConfigurableEnvironment environment,
        SpringApplication application
    ) {
        YamlMapFactoryBean yamlMapFactoryBean = new YamlMapFactoryBean();
        // 加载当前 starter 中的application.yml
        yamlMapFactoryBean.setResources(new ClassPathResource(CONFIG_SOURCE_NAME));
        Map<String, Object> properties = yamlMapFactoryBean.getObject();

        // 若想返回 properties 对象, 可以这样:
        // YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
//        Properties properties = factoryBean.getObject();

        log.debug(";; {} loaded.", CONFIG_SOURCE_NAME);


        MutablePropertySources propertySources = environment.getPropertySources();
//        propertySources.stream().filter(source -> source instanceof OriginTrackedMapPropertySource)
//            .map(raw -> (OriginTrackedMapPropertySource) raw);

        // Config resource 'class path resource [application.yml]' via location 'optional:classpath:/'
        /*
         * 配置源名称
         *  configurationProperties
         *  servletConfigInitParams
         *  servletContextInitParams
         *  systemProperties
         *  systemEnvironment
         *  random
         *  applicationConfig: [classpath:/config/application-druid.yml]
         *  applicationConfig: [classpath:/config/application.yml]
         * */
        propertySources.addBefore("Config resource 'class path resource [application.yml]' via location 'optional:classpath:/'",
            new OriginTrackedMapPropertySource("spring", properties));
    }
}