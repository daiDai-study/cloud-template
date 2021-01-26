package com.aac.kpi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.exception.AuthException;
import com.aac.kpi.system.property.SSOProperties;
import com.aac.kpi.system.service.SSOService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class SSOServiceImpl implements SSOService {

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");

    @Resource
    private SSOProperties ssoProperties;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public JsonNode getRemoteUserInfo(String token) {
        JsonNode jsonNode;
        try {
            jsonNode = this.doGetRemoteUserInfo(token);
        } catch (Exception e) {
            throw new AuthException("SSO 远程获取用户信息失败: " + e.getMessage());
        }
        JsonNode usernameNode;
        if ((usernameNode = jsonNode.get(ssoProperties.getUsername())) == null || StrUtil.isEmpty(usernameNode.asText())) {
            throw new AuthException("SSO 远程用户不存在");
        }
        JsonNode statuNode;
        if ((statuNode = jsonNode.get(ssoProperties.getStatus())) == null || !"1".equals(statuNode.asText())) {
            throw new AuthException("SSO 远程用户已被冻结");
        }
        return jsonNode;
    }

    private JsonNode doGetRemoteUserInfo(String token) throws Exception {
        // 动态调用webservice
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient(ssoProperties.getWsdlUrl());
        Object[] objects = new Object[0];
        try {
            objects = client.invoke("GetUserInfo", token);
        } catch (Exception e) {
            log.error("SSO 远程获取用户信息失败：{}", e.getMessage());
            throw e;
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                log.error("SSO 远程 WebService 客户端关闭失败：{}", e.getMessage());
            }
        }
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(objects[0].toString());
        } catch (JsonProcessingException e) {
            log.error("JSON字符串数据转换失败：{}", e.getMessage());
            throw e;
        }

        return jsonNode;
    }



    @Override
    public String ssoLogin(String username, String password) {
        // 1.登录，获取登录结果代码
        String userLoginInfo = "txtUserNo=" + username + "&txtPass=" + password + "&txtEffectiveTime=720&txtUrl=";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(userLoginInfo, MEDIA_TYPE);
        Request requestLogin = new Request.Builder()
                .url(ssoProperties.getLoginUrl())
                .post(requestBody)
                .build();
        Response responseLogin;
        String responseCode = null;
        try {
            responseLogin = client.newCall(requestLogin).execute();
            ResponseBody responseBody = responseLogin.body();
            if(responseBody != null){
                responseCode = responseBody.string();
            }
        } catch (IOException e) {
            throw new AuthException("SSO 登录失败：{}" + e.getMessage());
        }
        if (!"1".equals(responseCode)) {
            throw new AuthException("SSO 登录失败：用户名或密码错误");
        }else{
            log.info("用户{} SSO 登录成功: ", username);

            // 2.带cookie访问验证，获取token
            Headers headers = responseLogin.headers();
            List<Cookie> cookies = Cookie.parseAll(requestLogin.url(), headers);
            StringBuilder cookieStr = new StringBuilder();
            for (Cookie cookie : cookies) {
                cookieStr.append(cookie.name()).append("=").append(cookie.value()).append(";");
            }

            Request requestGetToken = new Request.Builder()
                    .url(ssoProperties.getValidateUrl())
                    .header("Cookie", cookieStr.toString())
                    .build();

            try {
                Response responseGetToken = client.newCall(requestGetToken).execute();
                String result = responseGetToken.request().url().toString();
                return result.split("=")[1]; // token
            } catch (IOException e) {
                throw new AuthException("SSO 登录成功，获取 SSO Token 失败：{}" + e.getMessage());
            }
        }
    }
}
