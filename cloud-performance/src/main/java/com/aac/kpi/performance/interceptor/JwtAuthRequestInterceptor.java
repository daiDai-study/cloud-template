package com.aac.kpi.performance.interceptor;

import com.aac.kpi.common.constant.CommonConst;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class JwtAuthRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
        if(attributes == null){
            return;
        }
        HttpServletRequest req = attributes.getRequest();
        // 如果header没有auth头，从cookie获取token
        String token = req.getHeader(CommonConst.TOKEN_HEADER);
//        Cookie[] cookies = req.getCookies();
//        if (cookies != null && cookies.length > 0) {
//            for (Cookie cookie : cookies) {
//                if (Objects.equals(cookie.getName(), "token")) {
//                    try {
//                        token = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.name());
//                    } catch (UnsupportedEncodingException e) {
//                        log.error(LogUtil.getStack(e));
//                    }
//                }
//            }
//        }
        requestTemplate.header(CommonConst.TOKEN_HEADER, token);
    }
}
