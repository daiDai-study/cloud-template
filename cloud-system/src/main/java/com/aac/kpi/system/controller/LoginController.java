package com.aac.kpi.system.controller;

import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.constant.CommonConst;
import com.aac.kpi.common.exception.AuthException;
import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.common.util.JwtUtil;
import com.aac.kpi.system.constant.SystemConst;
import com.aac.kpi.system.entity.SysRole;
import com.aac.kpi.system.entity.SysUser;
import com.aac.kpi.system.model.vo.LoginUser;
import com.aac.kpi.system.property.SSOProperties;
import com.aac.kpi.system.service.SSOService;
import com.aac.kpi.system.service.SysPermissionService;
import com.aac.kpi.system.service.SysRoleService;
import com.aac.kpi.system.service.SysUserService;
import com.aac.kpi.system.util.PasswordUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class LoginController extends BaseUserController {

    @Value("${app.name}")
    private String appName;
    @Value("${app.description}")
    private String appDescription;

    @Resource
    private SSOProperties ssoProperties;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private SSOService ssoService;
    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysPermissionService sysPermissionService;

    @Resource
    public void setSysUserService(SysUserService sysUserService){
        this.sysUserService = sysUserService;
    }

    @PostMapping(value = "/login")
    public ApiResult login(@RequestBody LoginUser loginUser) {
        String loginUsername = loginUser.getUsername();
        String loginPassword = loginUser.getPassword();

        // 查询本地库是否存在该用户
        SysUser sysUser = sysUserService.getByUsername(loginUsername);
        if (sysUser != null) {
            // 用户存在
            if (!SystemConst.USER_STATUR_NORMAL.equals(sysUser.getStatus())) {
                throw new AuthException("该账号被冻结，无法登录系统");
            }
        } else {
            // 用户不存在
            if (!ssoProperties.getIsOpen()) {
                // 不使用sso
                throw new AuthException("该用户不存在");
            } else {
                // 远端登录，并将远端信息拉去存入本地库，将来就可以实现本地登录
                // 1. sso远端登录，
                String token = ssoService.ssoLogin(loginUsername, loginPassword);

                // 2. 获取远端用户信息，以更新或者保存到本地
                JsonNode jsonNode = ssoService.getRemoteUserInfo(token);

                sysUser = addSSOUserInfo(jsonNode, loginPassword);
            }
        }

        // 密码验证
        String loginPasswordEncrypt = getPasswordEncrypt(sysUser, loginPassword);
        String password = sysUser.getPassword();
        // 5.验证本地密码不成功，则远程登录，并更新密码到本地
        if (!password.equals(loginPasswordEncrypt)) {
            // 密码不同，用户必然存在，因为用户如果不存在，则会添加用户信息（包括用户密码），之后又获取出来，必定相同

            // 1. sso远端登录
            ssoService.ssoLogin(loginUser.getUsername(), loginUser.getPassword());

            if (StrUtil.isNotEmpty(loginPasswordEncrypt)) {
                // 2.更新密码到本地
                sysUser.setPassword(loginPasswordEncrypt);
                sysUserService.updateById(sysUser);

                // 3.更新密码之后，重新查询用户
                sysUser = sysUserService.getByUsername(loginUsername);
            }
        }

        return loginSuccess(sysUser);
    }

    private String getPasswordEncrypt(SysUser sysUser, String loginPassword) {
        // 检验盐值的字节
        byte[] bytes = sysUser.getSalt().getBytes();
        if(bytes.length != 8){
            // PasswordUtil.encrypt 要求盐值的字节必须为8，所以需要重置
            String salt = PasswordUtil.getSalt();
            sysUser.setSalt(salt);
            sysUserService.updateById(sysUser);
        }

        return PasswordUtil.encrypt(sysUser.getUsername(), loginPassword, sysUser.getSalt());
    }


    @GetMapping(value = "/login/{ssoToken}")
    public ApiResult loginBySSOToken(@PathVariable("ssoToken") String ssoToken) {

        // 2. 获取远端用户信息，以更新或者保存到本地
        JsonNode jsonNode = ssoService.getRemoteUserInfo(ssoToken);
        String username = jsonNode.get(ssoProperties.getUsername()).toString();

        //查询本地库是否存在该用户,如不存在，则自动创建
        SysUser sysUser = sysUserService.getByUsername(username);
        if (sysUser == null) {
            sysUser = addSSOUserInfo(jsonNode, SystemConst.DEFAULT_PASSWORD);
        } else if (sysUser.getStatus() != 1) {
            return ApiResult.ofFailServer("该账号被冻结，无法登录");
        }

        return loginSuccess(sysUser);
    }

    @GetMapping(value = "/loginByUserInfo")
    public ApiResult loginByUserInfo(@RequestParam("username") String username, @RequestParam("realname") String realname) {

        //查询本地库是否存在该用户,如不存在，则自动创建
        SysUser sysUser = sysUserService.getByUsername(username);
        if (sysUser == null) {
            sysUser = addUserInfo(username, realname, SystemConst.DEFAULT_PASSWORD);
        } else if (sysUser.getStatus() != 1) {
            return ApiResult.ofFailServer("该账号被冻结，无法登录");
        }

        return loginSuccess(sysUser);
    }

    private SysUser addSSOUserInfo(JsonNode ssoUserInfo, String loginPassword) {
        String username;
        String realname;
        try {
            username = ssoUserInfo.get(ssoProperties.getUsername()).toString();
            realname = ssoUserInfo.get(ssoProperties.getRealname()).toString();
        } catch (Exception e) {
            throw new AuthException("SSO 远程用户信息没有域账号和姓名");
        }
        return addUserInfo(username, realname, loginPassword);
    }

    private SysUser addUserInfo(String username, String realname, String loginPassword) {
        SysUser user = new SysUser();

        user.setUsername(username);
        user.setRealname(realname);

        user.setStatus(SystemConst.USER_STATUR_NORMAL);
        user.setPassword(PasswordUtil.getEncrypt(user, loginPassword));

        // 用户的默认角色设置为普通用户
        SysRole sysRole = sysRoleService.getByRoleCode(SystemConst.ROLE_CODE_USER);
        if(sysRole==null){
            throw new RuntimeException("系统中没有" + SystemConst.ROLE_CODE_USER + "角色编号，请联系系统管理员进行处理");
        }
        // 保存用户及角色
        sysUserService.addUserWithRole(user, sysRole.getId());

        // 远端登录成功后，查询本地库进行登录，这样今后可以直接绕开sso, 使用本地账户
        user = sysUserService.getByUsername(user.getUsername());

        return user;
    }

    private ApiResult loginSuccess(SysUser sysUser) {
        //生成token
        String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getPassword());
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("token", token);
        objectNode.putPOJO("userInfo", sysUser);
        return ApiResult.ofSuccess(objectNode);
    }

    @GetMapping(value = "/getAppData")
    public JsonNode getAppData(HttpServletRequest request) {
        String token = request.getHeader(CommonConst.TOKEN_HEADER);
        if (token != null && token.startsWith(CommonConst.TOKEN_BEARER)) {
            token = token.substring(7);
        }
        ObjectNode objectNode = objectMapper.createObjectNode();

        SysUser currentUser = this.getCurrentUser(request);

        ObjectNode userInfo = objectMapper.createObjectNode();
        userInfo.put("username", currentUser.getUsername());
        List<SysRole> roleList = sysRoleService.listByUsername(currentUser.getUsername());
        StringBuilder roles = new StringBuilder();
        for (SysRole sysRole : roleList) {
            roles.append(sysRole.getRoleCode()).append(",");
        }
        userInfo.put("role", roles.length() > 0 ? roles.substring(0, roles.length() - 1) : roles.toString());
        userInfo.put("name", currentUser.getRealname());
        userInfo.put("avatar", currentUser.getAvatar());
        userInfo.put("email", currentUser.getEmail());
        userInfo.put("token", token);

        objectNode.set("user", userInfo);
        objectNode.set("menu", sysPermissionService.getMenu(currentUser.getUsername()));
        ObjectNode app = objectMapper.createObjectNode();
        app.put("name", appName);
        app.put("description", appDescription);
        objectNode.set("app", app);
        return objectNode;
    }
}
