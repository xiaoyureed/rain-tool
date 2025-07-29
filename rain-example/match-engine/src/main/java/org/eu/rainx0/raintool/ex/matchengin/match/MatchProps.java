package org.eu.rainx0.raintool.ex.matchengin.match;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author xiaoyu
 * @time 2025/7/20 19:19
 */
@Data
@Component
@ConfigurationProperties(prefix = "rain.match")
public class MatchProps {
    private Map<String, CoinScale> symbols = new HashMap<>();

    @Data
    public static class CoinScale {
        private int scale;
        private int baseScale;
    }
}
