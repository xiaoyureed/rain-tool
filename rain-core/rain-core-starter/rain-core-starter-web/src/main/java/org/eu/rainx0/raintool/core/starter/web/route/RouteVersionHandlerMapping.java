package org.eu.rainx0.raintool.core.starter.web.route;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 16:58
 */
public class RouteVersionHandlerMapping extends RequestMappingHandlerMapping {

    /**
     * 针对 Controller 类（类级别注解）的自定义匹配条件
     * 若设置了注解, 则构造自定义的 RequestCondition
     */
    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        RouteVersion annotation = AnnotationUtils.findAnnotation(handlerType, RouteVersion.class);
        if (annotation == null) {
            return null;
        }
        return new RouteVersionCondition(annotation.value());
    }

    /**
     * 针对 Controller 方法（方法级别注解）的自定义匹配条件
     */
    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        RouteVersion annotation = AnnotationUtils.findAnnotation(method, RouteVersion.class);
        if (annotation == null) {
            return null;
        }
        return new RouteVersionCondition(annotation.value());
    }


}
