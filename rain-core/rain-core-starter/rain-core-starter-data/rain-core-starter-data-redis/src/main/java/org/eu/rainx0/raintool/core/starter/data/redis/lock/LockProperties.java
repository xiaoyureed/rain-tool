package org.eu.rainx0.raintool.core.starter.data.redis.lock;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 分布式锁配置类
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "raincloud.lock")
public class LockProperties {
    /**
     * 加锁失败抛出的异常信息
     * 支持spEL，允许的参数有方法的入参以及 #waitTime
     */
    String errorMsg = "系统繁忙，请稍后再试。";
    /**
     * 锁类型
     */
    private LockType type = LockType.REDIS_REENTRANT_LOCK;
    /**
     * 锁key的前缀
     */
    private String prefix = "@lock";
    /**
     * 加锁后自动释放时间（秒），默认自动续期
     */
    private int leaseTime = -1;
    /**
     * 加锁的最长等待时间，超过则抛出异常（秒）
     * 0代表加锁失败则立即抛出异常
     */
    private int waitTime = 2;


    public enum LockType {
        /**
         * Redis可重入锁（默认）
         */
        REDIS_REENTRANT_LOCK,
        /**
         * Redis公平锁
         */
        REDIS_FAIR_LOCK,
        /**
         * Redis自旋锁
         */
        REDIS_SPIN_LOCK,

    }

}