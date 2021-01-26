package com.aac.kpi.gateway.api.system;

import com.aac.kpi.common.model.ApiResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "kpi-system", path = "/system/user", fallback = UserClientFallback.class)
public interface UserClient {

    // 根据 username 获取用户授权信息
    @GetMapping(value = "/getAuthInfoByUsername")
    ApiResult<JsonNode> getAuthInfoByUsername(@RequestParam(name = "username") String username);
}
