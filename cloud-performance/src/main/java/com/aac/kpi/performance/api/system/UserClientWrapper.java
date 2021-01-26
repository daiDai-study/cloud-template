package com.aac.kpi.performance.api.system;

import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.exception.AuthException;
import com.aac.kpi.common.model.ApiResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserClientWrapper {

    @Resource
    private UserClient userClient;

    public JsonNode getCurrentUser(){
        return ApiResult.getData(userClient.getSubject());
    }

    public String getCurrentUsername(){
        JsonNode currentUser = getCurrentUser();
        JsonNode currentUsernameNode;
        String currentUsername;
        if(currentUser != null && (currentUsernameNode = currentUser.get("username")) != null && StrUtil.isNotEmpty(currentUsername = currentUsernameNode.asText()) ){
            return currentUsername;
        }
        throw new AuthException("当前用户不存在");
    }
}
