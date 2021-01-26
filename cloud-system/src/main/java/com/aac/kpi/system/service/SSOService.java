package com.aac.kpi.system.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface SSOService {

    // 获取当前登录用户的远端的相关信息
    JsonNode getRemoteUserInfo(String token);

    // 远程登录，并返回token
    String ssoLogin(String txtUserNo, String txtPass);
}
