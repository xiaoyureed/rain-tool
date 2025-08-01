package org.eu.rainx0.raintool.core.service.gateway.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 17:07
 */
@Component
@ConfigurationProperties(prefix = "rain.gateway")
@Data
public class GatewayProps {

    private Auth auth = new Auth();

    private Trace trace = new Trace();

    @Data
    public static class Trace {
        /**
         * Trace key 在Redis中有效时间，单位秒, 默认5分钟
         */
        private long expired = 60 * 5;

        /**
         * refresh threshold
         *
         * Trace key 在Redis中有效时间还是剩余多长时间，就需要进行更新，单位秒, 默认1分钟
         * 即，如果Trace key在Redis中过期时间小于60秒，那么就重新创建Trace key
         */
        private long threshold = 60;
    }

    @Data
    public static class Auth {
        private boolean enabled = true;
        private List<String> whiteList = new ArrayList<>();
    }
}