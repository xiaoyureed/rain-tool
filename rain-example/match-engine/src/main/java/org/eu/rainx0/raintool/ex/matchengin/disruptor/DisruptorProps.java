package org.eu.rainx0.raintool.ex.matchengin.disruptor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author xiaoyu
 * @time 2025/7/20 11:35
 */
@Data
@Component
@ConfigurationProperties(prefix = "rain.match.disruptor")
public class DisruptorProps {
    private int ringBufferSize = 1024 * 1024;
    private boolean multiProducer = false;
}
