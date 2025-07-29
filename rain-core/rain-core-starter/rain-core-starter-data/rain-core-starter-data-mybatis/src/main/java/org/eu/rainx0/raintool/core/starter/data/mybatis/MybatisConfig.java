package org.eu.rainx0.raintool.core.starter.data.mybatis;

import java.util.Optional;

import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.eu.rainx0.raintool.core.starter.data.mybatis.model.MybatisProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

/**
 * @author xiaoyu
 * @time 2025/7/27 16:42
 */
@Configuration
// @MapperScan("xxx") // 手动指定@Mapper 注解亦可
public class MybatisConfig {


    @Autowired
    private MybatisProps mybatisProps;


    // @Bean
    // public ConfigurationCustomizer configurationCustomizer() {
    //     return configuration -> {
    //         configuration.setLogImpl(StdOutImpl.class);
    //     };
    // }
    @Bean
    @Primary
    public MybatisPlusPropertiesCustomizer mybatisPlusPropertiesCustomizer() {

        return props -> {
            MybatisConfiguration config = props.getConfiguration();

            if (config == null) {
                config = new MybatisConfiguration();
                props.setConfiguration(config);
            }

            config.setLogImpl(mybatisProps.isPrintSql() ? StdOutImpl.class : NoLoggingImpl.class);

        };


    }

    /**
     * mybatis plus plugins can be added here
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));

        return mybatisPlusInterceptor;
    }

    /**
     *
     * customize primary key generator
     */
//    @Bean
    public IdentifierGenerator identifierGenerator() {
        return new IdentifierGenerator() {
            @Override
            public Number nextId(Object entity) {
                return null;
            }
        };
    }
}
