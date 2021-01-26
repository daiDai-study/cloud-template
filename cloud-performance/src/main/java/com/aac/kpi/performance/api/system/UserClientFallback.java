package com.aac.kpi.performance.api.system;

import com.aac.kpi.common.model.ApiResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public ApiResult<JsonNode> getSubject() {
        return ApiResult.ofFailServer("system 服务出现异常，进行降级");
    }
}
