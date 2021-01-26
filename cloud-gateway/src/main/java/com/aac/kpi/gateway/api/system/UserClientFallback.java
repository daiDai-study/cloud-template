package com.aac.kpi.gateway.api.system;

import com.aac.kpi.common.model.ApiResult;

public class UserClientFallback implements UserClient {
    @Override
    public ApiResult getAuthInfoByUsername(String username) {
        return ApiResult.ofFailServer("system 服务出现异常，进行降级");
    }
}
