package org.eu.rainx0.raintool.ex.websocket.tomcat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author xiaoyu
 * @time 2025/8/8 17:55
 */
@Configuration
public class WsConfig {

    /**
     * 扫描所有 @ServerEndpoint 注解的 bean
     */
    @Bean
    ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
