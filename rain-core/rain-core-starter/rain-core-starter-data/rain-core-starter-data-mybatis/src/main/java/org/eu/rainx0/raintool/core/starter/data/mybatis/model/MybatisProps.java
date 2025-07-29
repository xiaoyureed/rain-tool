package org.eu.rainx0.raintool.core.starter.data.mybatis.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author xiaoyu
 * @time 2025/7/27 16:53
 */
@Component
@Data
@ConfigurationProperties(prefix = "rain.mybatis")
public class MybatisProps {
    private boolean printSql = true;
}
