package org.eu.rainx0.raintool.ex.influxdb.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaoyu
 * @time 2025/8/6 11:47
 */
@Configuration
public class InfluxdbConfig {
    @Bean
    InfluxDBClient influxDBClient() {
        InfluxDBClient client = InfluxDBClientFactory.create(
                "http://localhost:8086",
                "yvAfZRuoSvUq3SKueLiLHAY__jvRQq_ksCSvz5MRtS6wUQ_MS3HBF2Zm4_CjXwfJestt4Jl3PfRO4hPHpviM4Q==".toCharArray(),
                "hi", "b1"
        );

        return client;
    }
}
