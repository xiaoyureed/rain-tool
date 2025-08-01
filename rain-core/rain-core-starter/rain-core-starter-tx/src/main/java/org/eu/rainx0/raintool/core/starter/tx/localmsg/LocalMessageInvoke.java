package org.eu.rainx0.raintool.core.starter.tx.localmsg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可以保证方法一定成功执行
 * 若方法位于某个事务内, 会将方法记录入库, 重试保证执行
 * @author xiaoyu
 * @time 2025/7/31 11:22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LocalMessageInvoke {

    /**
     * 最大重试次数
     * @return
     */
    int retryTimes() default 3;

    /**
     * 是否异步执行
     * 默认异步执行，先入库，后续异步执行，不影响主线程快速返回结果
     * @return
     */
    boolean async() default true;
}
