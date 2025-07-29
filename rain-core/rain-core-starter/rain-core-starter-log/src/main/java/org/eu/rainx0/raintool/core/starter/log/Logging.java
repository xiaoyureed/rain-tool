package org.eu.rainx0.raintool.core.starter.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: xiaoyu
 * @time: 2025/6/29 18:36
 */
@Target({
    ElementType.METHOD,
//    ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Logging {
}
