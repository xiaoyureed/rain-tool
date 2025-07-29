package org.eu.rainx0.raintool.core.starter.data.jpa.config;

import org.springframework.context.annotation.Configuration;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 20:52
 */
@Configuration
public class DatabaseConfig {
    //    private static final String image = "postgres:alpine";
//
//    @Bean(initMethod = "start", destroyMethod = "stop")
//    public PostgreSQLContainer<?> postgreSQLContainer() {
//        return new PostgreSQLContainer<>(image).waitingFor(Wait.forListeningPort());
//    }
//
//    @Bean
//    @FlywayDataSource
//    public DataSource dataSource(PostgreSQLContainer<?> postgreSQLContainer) {
//        var hikariConfig = new HikariConfig();
//        hikariConfig.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
//        hikariConfig.setDriverClassName(postgreSQLContainer.getDriverClassName());
//        hikariConfig.setUsername(postgreSQLContainer.getUsername());
//        hikariConfig.setPassword(postgreSQLContainer.getPassword());
//
//        return new HikariDataSource(hikariConfig);
//    }
//
}
