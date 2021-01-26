package com.aac.kpi.projectmanage.aop;

import cn.hutool.json.JSONObject;
import com.aac.kpi.projectmanage.util.RequestHeaderInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //将头部信息都转换成map
        Map<String, String> headersInfo = new HashMap<>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = httpServletRequest.getHeader(key);
            headersInfo.put(key, value);
        }
        headersInfo.put("token", httpServletRequest.getHeader("Authorization"));

        RequestHeaderInfo.token = httpServletRequest.getHeader("Authorization");

        return true;
    }
}
