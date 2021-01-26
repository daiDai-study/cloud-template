package com.aac.kpi.gateway.auth.shiro.jwt;

import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.constant.CommonConst;
import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.common.util.JwtUtil;
import com.aac.kpi.gateway.api.system.UserClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class JwtRealm extends AuthorizingRealm {

    @Resource
    private UserClient userClient;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     *
     * 用于鉴权 ==> 获取用户角色和权限
     *
     * @param token token
     * @return AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection token) {
        String username = JwtUtil.getUsername(token.toString());

        if (StrUtil.isBlank(username)){
            throw new AuthenticationException("token校验不通过");
        }

        JsonNode data = getAuthInfoByUsername(username);
        Object role = data.get(CommonConst.ROLE_CODE_LIST);
        Object perm = data.get(CommonConst.PERMS_LIST);
        if(!(role instanceof List) || !(perm instanceof List)){
            throw new AuthenticationException("权限信息获取异常");
        }
        Set<String> roleCodeList = new HashSet<String>((List) role);
        Set<String> permsList = new HashSet<String>((List) perm);

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        // 获取用户角色集（模拟值，实际从数据库获取）
        simpleAuthorizationInfo.setRoles(roleCodeList);

        // 获取用户权限集（模拟值，实际从数据库获取）
        simpleAuthorizationInfo.setStringPermissions(permsList);
        return simpleAuthorizationInfo;
    }

    /**
     *
     * 登录 ==> 用户认证
     *
     * @param authenticationToken 身份认证 token
     * @return AuthenticationInfo 身份认证信息
     * @throws AuthenticationException 认证相关异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 这里的 token是从 JWTFilter 的 executeLogin 方法传递过来的，已经经过了解密
        String token = (String) authenticationToken.getPrincipal();

        String username = JwtUtil.getUsername(token);

        if (StrUtil.isBlank(username)){
            String message = "从token中获取不到用户名";
            log.error(message);
            throw new AuthenticationException(message);
        }

        JsonNode data = getAuthInfoByUsername(username);

        String password = data.get(CommonConst.PASSWORD).asText();

        if (!JwtUtil.verify(token, username, password)){
            String message = "token校验失败：用户名或密码错误";
            log.error(message);
            throw new AuthenticationException(message);
        }

        return new SimpleAuthenticationInfo(token, token, "jwtRealm");
    }

    private JsonNode getAuthInfoByUsername(String username){
        // 通过用户名查询用户信息
        return ApiResult.getData(userClient.getAuthInfoByUsername(username));
    }
}
