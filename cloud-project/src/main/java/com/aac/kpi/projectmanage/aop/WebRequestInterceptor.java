package com.aac.kpi.projectmanage.aop;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebRequestInterceptor implements WebMvcConfigurer {

    /**
     * 自定义拦截器()
     * @param registry
     */
    public void addInterceptors(InterceptorRegistry registry) {
        // RequestInterceptor为具体拦截逻辑的执行类 实现了HandlerInterceptor接口
        // addPathPatterns("/test/**")  意义是访问路径下/test 下所有的访问路径都需要被RequestInterceptor拦截
        // excludePathPatterns 这个访问路径/test/exception则不在被RequestInterceptor拦截的范围
        // /user/** user下所有路径都包含在内 例：/user/api 、/user/api/zz
        // /user/* 只有user下一层路径包含在内 例：/user/api(包含) 、/user/api/zz(不包含)
        // /test/queryUser接口则是token验证后，把token为xx的玩家信息放入Request中，方便接口拿取
        registry.addInterceptor(new RequestInterceptor())
                .addPathPatterns("/**");
    }
}
