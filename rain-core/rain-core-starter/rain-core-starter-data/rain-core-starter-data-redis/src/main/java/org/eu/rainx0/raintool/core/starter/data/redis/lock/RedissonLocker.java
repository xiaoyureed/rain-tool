package org.eu.rainx0.raintool.core.starter.data.redis.lock;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redisson加锁实现
 */
@AllArgsConstructor
@Slf4j
public class RedissonLocker implements ILocker {

    private static final ThreadLocal<RLock> LOCK_THREAD_LOCAL = new ThreadLocal<>();
    private final LockProperties.LockType lockType;
    private final RedissonClient redissonClient;

    @Override
    @SuppressWarnings("all")
    public boolean lock(String lockKey, int leaseTime, int waitTime, TimeUnit unit) throws InterruptedException {
        RLock lock = getLock(lockKey);
        LOCK_THREAD_LOCAL.set(lock);
        log.info("加锁，thread：{}，key：{}", Thread.currentThread().getId(), lockKey);
        return lock.tryLock(waitTime, leaseTime, unit);
    }

    @Override
    public void unlock() {
        log.info("解锁，thread：{}，key：{}", Thread.currentThread().getId(), LOCK_THREAD_LOCAL.get().getName());
        LOCK_THREAD_LOCAL.get().unlock();
        LOCK_THREAD_LOCAL.remove();
    }

    private RLock getLock(String lockKey) {
        switch (lockType) {
            case REDIS_FAIR_LOCK:
                return redissonClient.getFairLock(lockKey);
            case REDIS_SPIN_LOCK:
                return redissonClient.getSpinLock(lockKey);
            case REDIS_REENTRANT_LOCK:
                return redissonClient.getLock(lockKey);
            default:
                throw new IllegalArgumentException("错误的锁类型");
        }
    }
}