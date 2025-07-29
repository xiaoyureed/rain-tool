package org.eu.rainx0.raintool.core.starter.data.redis.cache.expire;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * 缓存过期注解（配合@Cachebale使用）
 * <p>
 * 1.支持防缓存穿透
 * 2.支持方法嵌套（内部方法如果不指定过期时间，则继承外部方法的过期时间）
 * 如果不使用该注解指定@Cacheable的过期时间，将会采取RedisCacheAutoConfiguration中配置的全局缓存失效时间
 *
 * @see CacheExpireAspect
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheExpire {

    /**
     * 失效时间（秒）
     */
    @AliasFor("inSeconds")
    long value() default 0;

    @AliasFor("value")
    int inSeconds() default 0;

    /**
     * 上下浮动范围（秒）
     * 根据浮动范围生成随机秒数加在缓存失效时间上，防止缓存穿透
     * 默认10毫秒
     */
    double floatRange() default 0.01;

}