package org.eu.rainx0.raintool.core.starter.log;

import java.time.Duration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/6/29 18:37
 */
@Aspect
@Slf4j
@Component // required
public class LoggingAop {

    /**
     * Define a Point cut, which named as 'loggingAnnotationPointcut',
     * for all methods that are annotated with @Logging
     */
    @Pointcut("@annotation(Logging)")
    public void loggingAnnotationPointcut() {}

    /**
     * Enhance the method with @Logging annotation
     */
    @Around("loggingAnnotationPointcut()")
    public Object printLog(ProceedingJoinPoint pjp) throws Throwable {
        long s = System.currentTimeMillis();

        Object[] args = pjp.getArgs();
        log.debug(";;Method start, args: {}", args);

        Object result = pjp.proceed();

        long e = System.currentTimeMillis();
        log.debug(";;Method end, duration: {}ms", Duration.ofMillis(e - s).toMillis());

        return result;
    }
}
