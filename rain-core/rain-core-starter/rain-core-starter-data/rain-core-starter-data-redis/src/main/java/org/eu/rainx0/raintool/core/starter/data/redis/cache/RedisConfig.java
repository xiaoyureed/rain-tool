package org.eu.rainx0.raintool.core.starter.data.redis.cache;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import org.eu.rainx0.raintool.core.starter.util.BeanTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 11:46
 */
// @Configuration(proxyBeanMethods = false) // 不为 @bean 方法创建cglib代理(即每次调用, 都是创建的新对象), 提高了性能
@Configuration
@EnableCaching
// 启用 aop (动态代理), 以支持 @Aspect切面编程
//目标类实现了接口 → 使用 JDK 动态代理, 未实现接口 → 使用 CGLIB 代理
@EnableAspectJAutoProxy(
    //Spring 会将目标类的 AOP 代理对象保存到线程上下文中（ThreadLocal）, 方便同个类的不同方法间嵌套调用时, 也可以拿到代理对象
    //如: 当前类方法内部通过 AopContext.currentProxy() 获取该代理对象
    exposeProxy = true

    // 强制使用 cglib;
    // ,proxyTargetClass = true
)
public class RedisConfig implements CachingConfigurer {

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Autowired
    private RedisConnectionFactory factory;

    @Bean
    public RedisSerializer<Object> redisSerializer() {
        return new Jackson2JsonRedisSerializer<>(BeanTools.objectMapper, Object.class);
    }

    /**
     * 注入RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 指定序列化方式
        //用于像 redisTemplate.opsForValue().set("myKey", "myValue") 这样的操作中 "myKey"
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(redisSerializer());

        //用于像 redisTemplate.opsForHash().put("myKey", "hashKey", value) 中的 "hashKey"
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(redisSerializer());

        redisTemplate.setConnectionFactory(factory);
        redisTemplate.afterPropertiesSet(); // 初始化, 检查是否都配置好了

        return redisTemplate;
    }

    /**
     * 注入RedisCacheManager (在这里做一些全局配置)
     *
     * EhCacheCacheManager	使用EhCache作为缓存技术
     * GuavaCacheManager	使用Google的GuavaCache作为缓存技术
     * RedisCacheManager	使用Redis作为缓存技术
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            //.disableCachingNullValues() // 禁止缓存空值 (默认配置是允许缓存null 的)
            // 接口缓存上指定了key的时候统一加服务名前缀
            .computePrefixWith(cacheName -> applicationName + ":" + cacheName + ":")
            // 设置全局过期时间 (默认是 1h)
            // 可以根据业务需要设置统一过期时间，这里是为了强制使用@CacheExpire手动设置过期时间所以设置很短
            .entryTtl(Duration.ofSeconds(60))
            // 配置序列化和反序列化方式
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer()));

        return RedisCacheManager.builder(factory).cacheDefaults(config).build();
       // return RedisCacheManager.builder(new ExpireRedisCacheWriter(factory)).cacheDefaults(config).build();
    }

    /**
     * 自带的SimpleKeyGenerator和key生成器不能满足需求，例如在参数为map时，要求map里的所有key-value组合作为一个key来做缓存；
     *
     * 如果手动指定了key, 则这个 generator 就没用了, 没指定的话, 在使用的地方，利用注解中的keyGenerator来指定key生成策略
     *          @Cacheable(value = "vv", keyGenerator = "keyGenerator")
     * 建议使用@Cacheable的时候都指定key
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {


                // 分隔符
                String separatorForClassAndMethod = ".";
                String separatorForParams = ",";
                String[] wrapperForParams = {"(", ")"};

                StringBuilder result = new StringBuilder(target.getClass().getSimpleName() +
                    separatorForClassAndMethod + method.getName());

                if (params.length == 0) {
                    return result.toString();
                }

                if (params.length == 1) {
                    Object param = params[0];

                    if (param instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) param;
                        if (map.isEmpty()) {
                            return result.append(wrapperForParams[0])
                                .append("emptyMap").append(wrapperForParams[1]).toString();
                        }

                        String collect = map.entrySet().stream()
                            .map(entry -> entry.getKey() + "=" + entry.getValue())
                            .collect(Collectors.joining(separatorForParams, wrapperForParams[0], wrapperForParams[1]));
                        result.append(collect);

                        return result.toString();
                    }
                }

                return result.append(wrapperForParams[0])
                    .append(BeanTools.toJson(params))
                    .append(wrapperForParams[1]);
            }
        };
//        return (target, method, params) -> Stream.of(params).map(String::valueOf).collect(Collectors.joining(","));
    }
}
