package com.aac.kpi.gateway.filter;

import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.gateway.auth.config.ShiroConfig;
import com.aac.kpi.gateway.auth.shiro.jwt.JwtFilter;
import com.aac.kpi.gateway.property.AuthProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

@Component
@Slf4j
public class AuthJwtGlobalFilter extends JwtFilter implements GlobalFilter, Ordered {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private AuthProperties authProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        log.info("access url:{}", url);

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        ServletRequest servletRequest = null;
        ServletResponse servletResponse = null;
        boolean convertSuccess = false;

        // org.springframework.http.server.ServletServerHttpRequest
        if((request instanceof ServletServerHttpRequest) && (response instanceof ServletServerHttpResponse)){
            servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            servletResponse =  ((ServletServerHttpResponse) response).getServletResponse();
            convertSuccess = true;
        }

        // org.springframework.http.server.reactive.AbstractServerHttpRequest
        if((request instanceof AbstractServerHttpRequest) && (response instanceof AbstractServerHttpResponse)){
            // AbstractServerHttpRequest/AbstractServerHttpResponse 不一定能得到 servletRequest/servletResponse
            try{
                servletRequest = ((AbstractServerHttpRequest) request).getNativeRequest();
                servletResponse =  ((AbstractServerHttpResponse) response).getNativeResponse();
                convertSuccess = true;
            }
            catch (Exception e){
                log.error(e.getMessage());
            }
        }

        if (!isMatch(url, authProperties.getAnonUrls()) && isMatch(url, authProperties.getJwtUrls()) && convertSuccess){
            // 进行 jwtFilter 认证
            if(!this.isAccessAllowed(servletRequest, servletResponse, null)) {
                // 401 返回
                response.getHeaders().add("Content-Type", "application/json; charset=utf-8");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.putPOJO("result", ApiResult.ofFailClientUnauthorized());
                JsonNode jsonNode = objectNode.get("result");
                DataBuffer buffer = response.bufferFactory().wrap(jsonNode.toPrettyString().getBytes());
                return response.writeWith(Mono.just(buffer));
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
