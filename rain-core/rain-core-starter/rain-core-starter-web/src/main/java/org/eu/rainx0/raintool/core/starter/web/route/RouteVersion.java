package org.eu.rainx0.raintool.core.starter.web.route;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 13:31
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RouteVersion {
    /**
     * 版本号, v1, v2...
     */
    String value();
}
