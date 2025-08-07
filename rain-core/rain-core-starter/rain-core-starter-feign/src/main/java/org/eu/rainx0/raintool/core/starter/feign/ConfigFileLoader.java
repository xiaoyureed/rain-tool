package org.eu.rainx0.raintool.core.starter.feign;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author xiaoyu
 * @time 2025/7/29 11:08
 */
@Configuration
public class ConfigFileLoader implements EnvironmentPostProcessor {

    private static final String config_file = "application-feign.yml";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        try {
            Resource resource = new ClassPathResource(config_file);
            List<PropertySource<?>> sources = loader.load(config_file, resource);

            for (PropertySource<?> s : sources) {
                environment.getPropertySources().addLast(s); // addFirst 添加的 props 有更高的优先级, 这里有意降低优先级, 可以被覆盖
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load " + config_file, e);
        }
    }
}
