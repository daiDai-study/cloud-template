package com.aac.kpi.performance.api.system;

import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.performance.interceptor.JwtAuthRequestInterceptor;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@FeignClient(name = "kpi-system", path = "/system/user", fallback = UserClientFallback.class, configuration = JwtAuthRequestInterceptor.class)
public interface UserClient {

    // 获取当前登录用户
    @GetMapping(value = "/getSubject")
    ApiResult<JsonNode> getSubject();
}
