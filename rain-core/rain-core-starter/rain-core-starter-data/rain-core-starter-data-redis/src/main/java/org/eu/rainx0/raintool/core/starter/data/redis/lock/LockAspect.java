package org.eu.rainx0.raintool.core.starter.data.redis.lock;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.eu.rainx0.raintool.core.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 分布式锁AOP
 * @see Lock
 */
@Aspect
@Slf4j
@Component
public class LockAspect {
    private static final SpelExpressionParser SPEL_PARSER = new SpelExpressionParser();
    @Autowired
    private LockProperties lockProperties;

    @Autowired
    private ILocker locker;

    /**
     * "@annotation(xxx)" 中, xxx 表示注解, 可以是类型, 可以是变量名, 这里是变量名, 注入到方法参数中了
     */
    @Around("@annotation(lockAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, Lock lockAnnotation) throws Throwable {
        // 加锁
        String lockKey = generateLockKey(joinPoint, lockAnnotation);
        log.debug("准备加锁，thread：{}，key：{}", Thread.currentThread().getId(), lockKey);
        long l = System.currentTimeMillis();
        boolean isLock = locker.lock(lockKey, lockAnnotation.leaseTime(), lockAnnotation.waitTime(), lockAnnotation.unit());
        log.debug("加锁完成，thread：{}，key：{}，结果{}", Thread.currentThread().getId(), lockKey, isLock);
        if (isLock) {
            try {
                return joinPoint.proceed();
            } finally {
                // 解锁
                locker.unlock();
            }
        } else {
            // 加锁失败，执行抛异常
            long waitTime = lockAnnotation.waitTime();
            String errorMsg = lockAnnotation.errorMsg();
            log.warn("获取锁超时，@Lock位置：{}，设置等待时间{}，实际等待时间{}",
                getClassAndMethodName(joinPoint), waitTime, System.currentTimeMillis() - l);
            boolean isDefault = "${spring.lock.errorMsg}".equals(errorMsg);
            if (isDefault) {
                errorMsg = lockProperties.getErrorMsg();
            }
            boolean isSpringEl = errorMsg.contains("+") && errorMsg.contains("#");
            if (isSpringEl) {
                // 存储上下文参数信息，用于解析spEL表达式
                EvaluationContext evaluationContext = new StandardEvaluationContext();
                // 如果是spEL表达式就解析它
                evaluationContext.setVariable("waitTime", waitTime);
                errorMsg = SPEL_PARSER.parseExpression(errorMsg).getValue(evaluationContext, String.class);
            }
            throw new BizException(errorMsg);
        }
    }

    /**
     * 生成redis key
     * <p>
     * 格式为：@lock:[group]:([key1],[key2]...)
     */
    @SuppressWarnings("all")
    private String generateLockKey(ProceedingJoinPoint joinPoint, Lock lockAnnotation) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String[] lockKey = getLockKey(lockAnnotation);

        String classAndMethodName = getClassAndMethodName(joinPoint);
        String lockKeyPrefix = lockProperties.getPrefix() + ":" + classAndMethodName;
        if (lockKey.length == 0) {
            // 未配置@Lock的key属性
            return lockKeyPrefix;
        } else {
            // 解析springEl
            String[] parameterNames = this.getParameterNames(method);
            if (parameterNames == null || parameterNames.length == 0) {
                // 方法没有入参
                log.warn("{}方法没有入参，@Lock配置的key属性将被忽略", classAndMethodName);
                return lockKeyPrefix;
            }

            EvaluationContext evaluationContext = new StandardEvaluationContext();
            // 获取方法参数值
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                // 替换表达式里的变量值为实际值，p0,p1或者原参数名
                evaluationContext.setVariable(parameterNames[i], args[i]);
                evaluationContext.setVariable("p" + i, args[i]);
            }
            try {
                return Stream.of(lockKey)
                    .map(exp -> SPEL_PARSER.parseExpression(exp).getValue(evaluationContext, String.class))
                    .collect(Collectors.joining(",", lockKeyPrefix + ":(", ")"));
            } catch (RuntimeException e) {
                throw new EvaluationException(classAndMethodName + "上的注解@Lock的key属性指定有误，无法解析spEl表达式：" + classAndMethodName, e);
            }
        }
    }

    private String[] getParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        String[] parameterNames = new String[parameters.length];

        for(int i = 0; i < parameters.length; ++i) {
            Parameter param = parameters[i];
            if (!param.isNamePresent()) {
                return null;
            }

            parameterNames[i] = param.getName();
        }

        return parameterNames;
    }

    private String[] getLockKey(Lock lock) {
        int keyLength = lock.key().length;
        return keyLength > 0 ? lock.key() : lock.value();
    }

    private String getClassAndMethodName(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return "@" + joinPoint.getTarget().getClass().getName() + "." + method.getName();
    }


}