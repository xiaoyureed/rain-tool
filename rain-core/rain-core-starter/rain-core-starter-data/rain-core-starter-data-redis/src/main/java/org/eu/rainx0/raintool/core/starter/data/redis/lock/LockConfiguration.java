package org.eu.rainx0.raintool.core.starter.data.redis.lock;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 13:27
 */
@Configuration(proxyBeanMethods = false) // this means the instance get from container is always new one
@EnableConfigurationProperties({
    LockProperties.class
})
public class LockConfiguration {
    /**
     * 注入分布式锁，业务框架有需要也可以自己实现
     */
    @Bean
    @ConditionalOnMissingBean(ILocker.class)
    public ILocker locker(LockProperties lockProperties, RedissonClient redissonClient) {
        return new RedissonLocker(lockProperties.getType(), redissonClient);
    }
}
