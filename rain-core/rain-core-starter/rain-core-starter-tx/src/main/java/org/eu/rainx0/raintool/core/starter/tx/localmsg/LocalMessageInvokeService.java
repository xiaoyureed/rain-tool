package org.eu.rainx0.raintool.core.starter.tx.localmsg;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.eu.rainx0.raintool.core.starter.tx.localmsg.model.InvokeInfo;
import org.eu.rainx0.raintool.core.starter.tx.localmsg.model.LocalMessage;
import org.eu.rainx0.raintool.core.starter.util.SpringContextTools;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaoyu
 * @time 2025/7/31 11:48
 */
@Slf4j
public class LocalMessageInvokeService<ID, T extends LocalMessage<ID>> {

    private LocalMessageInvokeDao<ID, T> dao;

    private Executor executor;

    @Scheduled(cron = "*/5 * * * * ?")
    public void scheduleRetry() {
        List<T> retryMessages = dao.getRetryMessages();
        for (T msg : retryMessages) {

        }
    }

    private void prepareRetry(LocalMessage<ID> msg, String error) {
    }

    public void invoke(T msg, boolean async) {
        boolean inTx = TransactionSynchronizationManager.isActualTransactionActive();
        // 没有处于事务包裹中, 忽略
        if (!inTx) {
            return;
        }

        dao.save(msg);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            // 业务执行完, 再处理 local Message invoke
            @Override
            public void afterCommit() {
                if (async) {
                    executor.execute(() -> internalInvoke(msg));
                } else {
                    internalInvoke(msg);
                }
            }
        });
    }

    private void internalInvoke(LocalMessage<ID> msg) {
        InvokeInfo invokeInfo = msg.getInvokeInfo();

        LocalMessageInvokeHolder.setInvoking();
        try {
            Class<?> beanClass = Class.forName(invokeInfo.getClazz());
            Object bean = SpringContextTools.getBean(beanClass);

            List<Class<?>> paramTypes = parseParamTypes(invokeInfo.getParamTypes());
            Method method = ReflectUtils.findDeclaredMethod(beanClass, invokeInfo.getMethod(), paramTypes.toArray(new Class[]{}));

            ReflectionUtils.invokeMethod(method, bean, parseParamValues(paramTypes, invokeInfo.getParamValues()));

            dao.remove(msg.getId());
        } catch (Exception e) {
            log.error("LocalMessageInvoke failed: ", e);
            this.prepareRetry(msg, e.getMessage());
        } finally {
            LocalMessageInvokeHolder.setInvoked();
        }
    }

    private Object[] parseParamValues(List<Class<?>> paramTypes, String paramValues) {

        return null;

    }

    private List<Class<?>> parseParamTypes(String paramTypes) {
        return Arrays.stream(paramTypes.split(",")).map(String::trim)
            .filter(StringUtils::hasText)
            .map(s -> {
                try {
                    return Class.forName(s);
                } catch (ClassNotFoundException e) {
                    log.error("Class not found: ", e);
                    return null;
                }

            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

}
