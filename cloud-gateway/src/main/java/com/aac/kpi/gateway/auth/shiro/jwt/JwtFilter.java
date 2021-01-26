package com.aac.kpi.gateway.auth.shiro.jwt;

import cn.hutool.extra.spring.SpringUtil;
import com.aac.kpi.gateway.property.AuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.BearerToken;
import org.apache.shiro.web.filter.authc.BearerHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JwtFilter extends BearerHttpAuthenticationFilter {

    protected AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        AuthProperties authProperties = SpringUtil.getBean(AuthProperties.class);

        if(isMatch(httpServletRequest.getRequestURI(), authProperties.getAnonUrls())){
            return true;
        }

        if (isLoginAttempt(request, response)) {
            return executeLogin(request, response);
        }
        return false;
    }

    public boolean isMatch(String url, String[] matchUrls){
        boolean match = false;
        for (String u : matchUrls) {
            if (pathMatcher.match(u, url))
                match = true;
        }
        return match;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        AuthenticationToken token = createToken(request, response);
        if (!(token instanceof BearerToken)) {
            return false;
        }
        BearerToken bearerToken = (BearerToken) token;
        JwtToken jwtToken = new JwtToken(bearerToken);
        try {
            getSubject(request, response).login(jwtToken);
            return true;
        } catch (Exception e) {
            log.error("token登录失败：{}", e.getMessage());
            return false;
        }
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个 option请求，这里我们给 option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
