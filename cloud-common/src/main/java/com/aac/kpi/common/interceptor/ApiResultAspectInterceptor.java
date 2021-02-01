package com.aac.kpi.common.interceptor;

import com.aac.kpi.common.aspect.ApiResultAspect;
import com.aac.kpi.common.constant.CommonConst;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class ApiResultAspectInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 拦截请求方法，如果请求方法所在的类或其本身添加了指定注解（ApiResultAspect），则为该请求添加属性，留给返回 response 时进行判断
        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 拿到要执行的类
            final Class<?> clazz = handlerMethod.getBeanType();
            // 拿到要执行的方法
            final Method method = handlerMethod.getMethod();
            // 判断是否在类对象上添加了注解
            if (clazz.isAnnotationPresent(ApiResultAspect.class)) {
                // 设置请求属性，留待在 ResponseBodyAdvice 进行判断
                request.setAttribute(CommonConst.RESPONSE_API_RESULT_ANN, clazz.getAnnotation(ApiResultAspect.class));
                // 判断是否在方法体上添加了注解
            } else if (method.isAnnotationPresent(ApiResultAspect.class)) {
                request.setAttribute(CommonConst.RESPONSE_API_RESULT_ANN, method.getAnnotation(ApiResultAspect.class));
            }
        }
        return true;
    }
}
