package com.aac.kpi.common.config;

import com.aac.kpi.common.handler.ApiResultAspectHandler;
import com.aac.kpi.common.interceptor.ApiResultAspectInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
@ConditionalOnBean({ApiResultAspectInterceptor.class})
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private ApiResultAspectInterceptor responseResultInterceptor;

    public InterceptorConfig() {
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有的请求
        String apiUri = "/**";
        registry.addInterceptor(this.responseResultInterceptor).addPathPatterns(apiUri);
    }
}
