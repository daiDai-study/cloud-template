package com.aac.kpi.system.handler;

import com.aac.kpi.common.exception.AuthException;
import com.aac.kpi.common.exception.BizException;
import com.aac.kpi.common.model.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(value = NullPointerException.class)
    public ApiResult handlerNullPointerException(HttpServletRequest req, NullPointerException e){
        log.error("空指针异常：{}", e.getMessage(), e);
        return ApiResult.ofFailServer("空指针异常");
    }

    @ExceptionHandler(value = BizException.class)
    public ApiResult handlerBizException(HttpServletRequest req, BizException e){
        log.error("业务异常：{}", e.getMessage(), e);
        return ApiResult.ofFailServer("业务异常");
    }

    @ExceptionHandler(value = AuthException.class)
    public ApiResult handlerAuthException(HttpServletRequest req, AuthException e){
        log.error("认证或鉴权异常：{}", e.getMessage(), e);
        return ApiResult.ofFailServer("认证或鉴权异常: " + e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ApiResult handlerBizException(HttpServletRequest req, Exception e){
        log.error("异常：{}", e.getMessage(), e);
        return ApiResult.ofFailServer("异常");
    }
}
