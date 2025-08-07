package org.eu.rainx0.raintool.core.starter.netty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/8/7 21:25
 */
@Component
@Data
@ConfigurationProperties(prefix = "rain.netty")
public class NettyProps {
    private Client client = new Client();

    private Server server = new Server();

    @Data
    static class Client {

    }

    @Data
    static class Server {
        private int port;
        private int maxReadIdleSeconds = 10;

    }
}
