package com.aac.kpi.system.controller;

import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.constant.CommonConst;
import com.aac.kpi.common.exception.AuthException;
import com.aac.kpi.common.util.JwtUtil;
import com.aac.kpi.system.constant.SystemConst;
import com.aac.kpi.system.entity.SysUser;
import com.aac.kpi.system.service.SysUserService;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseUserController {

    protected SysUserService sysUserService;

    public SysUser getCurrentUser(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(CommonConst.TOKEN_HEADER);
        String[] prinCred = null;
        String token = "";
        if (authorizationHeader != null) {
            String[] authTokens = authorizationHeader.split(" ");
            prinCred = authTokens.length >= 2 ? this.getPrincipalsAndCredentials(authTokens[0], authTokens[1]) : null;
            if (prinCred != null && prinCred.length >= 1) {
                token = prinCred[0] != null ? prinCred[0] : "";
            }
        }
        String username = JwtUtil.getUsername(token);

        if (StrUtil.isBlank(username)) {
            throw new AuthException("token校验不通过");
        }

        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new AuthException("用户不存在");
        }
        return user;
    }

    private String[] getPrincipalsAndCredentials(String scheme, String token) {
        return new String[]{token};
    }
}
