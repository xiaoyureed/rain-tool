package org.eu.rainx0.raintool.core.service.gateway.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * xiaoyureed@gmail.com
 * 路由动态刷新监听器, 自动生成 swagger api definition
 * 从 nacos 动态获取 groupOpenApi
 */
@Slf4j
@Component
public class RefreshRoutesListener implements ApplicationListener<RefreshRoutesEvent> {

    public static final String API_URI = "/v3/api-docs";

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RouteLocator routeLocator;
    @Autowired
    private SwaggerUiConfigParameters swaggerUiConfigParameters;
    @Autowired
    private SwaggerUiConfigProperties swaggerUiConfigProperties;

    {
        log.debug(";;RefreshRoutesListener is in use");
    }

    @Override
    public void onApplicationEvent(RefreshRoutesEvent event) {
        List<String> routes = new ArrayList<>();

        routeLocator.getRoutes()
            .filter(route -> route.getUri().getHost() != null
                && Objects.equals(route.getUri().getScheme(), "lb")
                && !applicationName.equalsIgnoreCase(route.getUri().getHost())
            )
            .subscribe(route -> routes.add(route.getUri().getHost()));

        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> swaggerUrls = routes.stream()
            .map(this::createSwaggerUrl)
            .collect(Collectors.toSet());

        if (ObjectUtils.isNotEmpty(swaggerUiConfigParameters)) {
            log.debug(";; Services is Changed, update Urls");

            swaggerUiConfigParameters.setUrls(swaggerUrls);
            swaggerUiConfigProperties.setUrls(swaggerUrls);
        }
    }

    private AbstractSwaggerUiConfigProperties.SwaggerUrl createSwaggerUrl(String service) {
        String data = API_URI;

        if (StringUtils.containsIgnoreCase(service, "bpmn")) {
            data = "/openapi.json";
        }

        String url = "/" + service.toLowerCase() + data;

        log.debug(";;Create Swagger Url - Name: {}, Location {}.", service, url);

        AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl();
        swaggerUrl.setUrl(url);
        swaggerUrl.setName(service);

        return swaggerUrl;
    }
}
