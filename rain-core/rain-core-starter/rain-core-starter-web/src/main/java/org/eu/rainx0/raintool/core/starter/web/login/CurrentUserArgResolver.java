package org.eu.rainx0.raintool.core.starter.web.login;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author: xiaoyu
 * @time: 2025/6/29 22:02
 */
public class CurrentUserArgResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Ensure that the parameter is annotated with @CurrentUser
        // && the parameter is a subclass of ILoginUser
        return parameter.hasParameterAnnotation(CurrentUser.class) && ICurrentUser.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    //    todo
        return parameter;
    }
}
