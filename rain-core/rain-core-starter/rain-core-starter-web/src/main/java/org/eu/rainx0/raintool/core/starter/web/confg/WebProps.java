package org.eu.rainx0.raintool.core.starter.web.confg;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/7/2 14:01
 */
@Data
@ConfigurationProperties(prefix = "rain.web")
@Component
public class WebProps {
    private Auth auth = new Auth(); // 嵌套属性, 必须手动实例化
    private Swagger swagger = new Swagger();
    private boolean onlyStatic = false;

    @Data
    public static class Auth {
        private boolean enabled = false;
    }

    @Data
    public static class Swagger {
        private String version = "1.0";
        private String url;
        /**
         * spring security enabled or not, default to false
         */
        private boolean securityEnabled = false;
    }
}
