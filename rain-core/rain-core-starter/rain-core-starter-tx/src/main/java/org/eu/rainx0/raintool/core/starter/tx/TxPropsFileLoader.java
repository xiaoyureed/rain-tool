package org.eu.rainx0.raintool.core.starter.tx;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * @author xiaoyu
 * @time 2025/7/31 11:02
 */
@Component
public class TxPropsFileLoader {
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    void load() throws IOException {
        YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
        ClassPathResource resource = new ClassPathResource("application-seata.yml");
        List<PropertySource<?>> propertySources = yamlLoader.load("application-seata", resource);

        ((ConfigurableEnvironment) applicationContext.getEnvironment())
            .getPropertySources().addLast(propertySources.get(0));
    }
}
