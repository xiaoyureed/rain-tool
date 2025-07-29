package org.eu.rainx0.raintool.core.starter.async;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.ttl.threadpool.TtlExecutors;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 10:19
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {
    /**
     * ThreadPoolExecutor 来自Java 标准库
     * ThreadPoolExecutor executor = new ThreadPoolExecutor(
     *     2,  // corePoolSize
     *     4,  // maximumPoolSize
     *     60, // keepAliveTime
     *     TimeUnit.SECONDS,
     *     new LinkedBlockingQueue<>(100),
     *     new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
     * );
     *
     * ThreadPoolTaskExecutor 来自 spring 框架
     *  内部封装了 ThreadPoolExecutor
     *  增强:
     *      - 支持定时任务（通过集成 ScheduledExecutorService）
     *      - 支持优雅关闭
     *
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("async-task-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        executor.initialize();

        // TtlExecutors can be used to support passing vars in multi-threads env
        return TtlExecutors.getTtlExecutor(executor);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error(";;Error occurred when exec async task, exception: {}, method: {}, params: {}",
                ex.getMessage(),
                method.getName(),
                Arrays.stream(params).map(Object::toString).collect(
                    Collectors.joining(",", "(", ")")
                ),
                ex
            );
        };
    }
}
