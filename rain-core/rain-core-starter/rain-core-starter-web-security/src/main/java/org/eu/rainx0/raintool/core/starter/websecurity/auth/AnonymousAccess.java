package org.eu.rainx0.raintool.core.starter.websecurity.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记只允许匿名访问的 controller 方法
 *
 * @author: xiaoyu
 * @time: 2025/7/7 21:35
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnonymousAccess {
}
