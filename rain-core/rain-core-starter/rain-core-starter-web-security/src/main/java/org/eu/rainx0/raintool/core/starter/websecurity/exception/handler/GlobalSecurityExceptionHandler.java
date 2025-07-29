package org.eu.rainx0.raintool.core.starter.websecurity.exception.handler;

import org.eu.rainx0.raintool.core.common.CodeEnum;
import org.eu.rainx0.raintool.core.common.model.ResponseWrapper;
import org.eu.rainx0.raintool.core.starter.web.util.ServletTools;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

/**
 * (这种方式仅在请求到达 Controller 层后才生效, 而 Spring Security 的异常通常发生在 Filter 阶段
 * 所以要配合 GlobalSecurityExceptionCatchFilter 将 异常手动 resolve, 才能在这里统一处理
 * )
 * @author: xiaoyu
 * @time: 2025/7/7 21:48
 */
@ControllerAdvice
@Slf4j
public class GlobalSecurityExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseWrapper<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("❌当前登录用户权限不足, uri: {}, 异常信息: {}", ServletTools.getRequestInfo(), e.getMessage());
        return ResponseWrapper.error(CodeEnum.access_deny);
    }

    // @ResponseBody
    // @ExceptionHandler(UsernameNotFoundException.class)
    // public ResponseWrapper<Void> handleUserNotFoundException(UsernameNotFoundException e) {
    //     log.error("❌认证失败, 用户不存在, uri: {}, 异常信息: {}", ServletTools.getRequestInfo(), e.getMessage());
    //     return ResponseWrapper.error(CodeEnum.authentication_error);
    // }

    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public ResponseWrapper<Void> handleAuthenticationException(AuthenticationException e) {
        log.error("❌认证失败, uri: {}, 异常信息: {}", ServletTools.getRequestInfo(), e.getMessage(), e);
        return ResponseWrapper.error(CodeEnum.authentication_error);
    }
}
