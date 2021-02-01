package com.aac.kpi.system.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.aspect.ApiResultAspect;
import com.aac.kpi.common.constant.CommonConst;
import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.system.annotation.DictToData;
import com.aac.kpi.system.entity.SysRole;
import com.aac.kpi.system.entity.SysUser;
import com.aac.kpi.system.entity.SysUserRole;
import com.aac.kpi.common.model.DictModel;
import com.aac.kpi.system.model.vo.UserInfoVO;
import com.aac.kpi.system.service.SysPermissionService;
import com.aac.kpi.system.service.SysRoleService;
import com.aac.kpi.system.service.SysUserRoleService;
import com.aac.kpi.system.service.SysUserService;
import com.aac.kpi.system.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@ApiResultAspect
public class SysUserController extends BaseManagementController<SysUser, SysUserService> {
    @Resource
    private SysUserRoleService sysUserRoleService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysPermissionService sysPermissionService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    public void setS(SysUserService service){
        this.service = service;
        this.sysUserService = service;
    }


    @PostMapping(value = "/addUserInfo")
    public ApiResult addUserInfo(@RequestBody UserInfoVO userInfoVO) {
        service.addUserWithRole(userInfoVO);
        return ApiResult.ofSuccess(userInfoVO);
    }

    @PutMapping(value = "/editUserInfo")
    public ApiResult editUserInfo(@RequestBody UserInfoVO userInfoVO) {
        service.editUserWithRole(userInfoVO);
        return ApiResult.ofSuccess(userInfoVO);
    }

    @GetMapping(value = "/pageListUserInfo")
    @DictToData
    public ApiResult pageListUserInfo(UserInfoVO userInfoVO,
                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                              HttpServletRequest req) {
        Page<UserInfoVO> page = new Page<>(pageNo, pageSize);
        IPage<UserInfoVO> pageList = service.queryUserInfoVoWithPage(page, userInfoVO);
        return ApiResult.ofSuccess(pageList);
    }

    // 根据 username 获取用户信息
    @GetMapping(value = "/getAuthInfoByUsername")
    public ApiResult<JsonNode> getAuthInfoByUsername(@RequestParam(name = "username") String username) {
        SysUser model = service.getByUsername(username);
        if (model == null) {
            return ApiResult.ofFailClientNotFound();
        }
        List<String> sysRoleCodes = sysRoleService.listCodeByUsername(username);
//        ArrayNode roleCodeList = objectMapper.createArrayNode();
//        for (String sysRoleCode : sysRoleCodes) {
//            roleCodeList.add(sysRoleCode);
//        }
        List<String> listPerms = sysPermissionService.listPermsByUsername(username);
//        ArrayNode permsList = objectMapper.createArrayNode();
//        for (String perms : listPerms) {
//            permsList.add(perms);
//        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put(CommonConst.USERNAME, model.getUsername());
        objectNode.put(CommonConst.PASSWORD, model.getPassword());
        objectNode.putPOJO(CommonConst.ROLE_CODE_LIST, sysRoleCodes);
        objectNode.putPOJO(CommonConst.PERMS_LIST, listPerms);
        return ApiResult.ofSuccess(objectNode);
    }


    // 查询用户角色Name
    @GetMapping(value = "/listRoleName")
    public ApiResult listRoleName(@RequestParam(name = "userId") String userId) {
        List<String> roleNames = new ArrayList<>();
        List<SysUserRole> userRoles = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userId));
        if (!CollUtil.isEmpty(userRoles)) {
            List<String> roleIds = new ArrayList<>();
            for (SysUserRole sysUserRole : userRoles) {
                roleIds.add(sysUserRole.getRoleId());
            }
            List<SysRole> roles = sysRoleService.listByIds(roleIds);
            roles.forEach(i -> {
                roleNames.add(i.getRoleName());
            });
        }
        return ApiResult.ofSuccess(roleNames);
    }

    // 查询用户角色 Id
    @GetMapping(value = "/listRoleId")
    public ApiResult listRoleId(@RequestParam(name = "userId") String userId) {
        List<String> roleIds = new ArrayList<>();
        List<SysUserRole> userRoles = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userId));
        if (!CollUtil.isEmpty(userRoles)){
            for (SysUserRole sysUserRole : userRoles) {
                roleIds.add(sysUserRole.getRoleId());
            }
        }
        return ApiResult.ofSuccess(roleIds);
    }


    /**
     * 修改密码
     */
    @PutMapping(value = "/changPassword")
    public ApiResult changPassword(@RequestBody SysUser sysUser) {
        String password = sysUser.getPassword();
        sysUser = this.service.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()));
        if (sysUser == null) {
            return ApiResult.ofFailClientNotFound();
        } else {
            String salt = PasswordUtil.getSalt(8);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setSalt(salt);
            sysUser.setPassword(passwordEncode);
            this.service.updateById(sysUser);
            return ApiResult.ofSuccess(sysUser);
        }
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPassword")
    public ApiResult resetPassword(@RequestBody JsonNode json, HttpServletRequest request) {

        String oldPwd = json.get("oldPwd").toString();
        String newPwd = json.get("newPwd").toString();
        String confirmNewPwd = json.get("confirmNewPwd").toString();

        SysUser currentUser = this.getCurrentUser(request);
        if (StrUtil.isEmpty(oldPwd) || StrUtil.isEmpty(newPwd) || StrUtil.isEmpty(confirmNewPwd)) {
            return ApiResult.ofFailClient("密码不能为空");
        }
        if (!newPwd.equals(confirmNewPwd)) {
            return ApiResult.ofFailClient("新密码与确认新密码不一致");
        }
        //密码验证
        String inputPwd = PasswordUtil.encrypt(currentUser.getUsername(), oldPwd, currentUser.getSalt());
        String userPwd = currentUser.getPassword();
        if (!inputPwd.equals(userPwd)) {
            return ApiResult.ofFailClient("原始密码输入错误");
        }
        String salt = PasswordUtil.getSalt(8);
        String passwordEncode = PasswordUtil.encrypt(currentUser.getUsername(), newPwd, salt);
        currentUser.setSalt(salt);
        currentUser.setPassword(passwordEncode);
        this.service.updateById(currentUser);
        return ApiResult.ofSuccess(currentUser);
    }

    /**
     * 添加手机号
     */
    @PutMapping("/addPhone")
    public ApiResult addPhone(@RequestBody JsonNode json, HttpServletRequest request) {
        String phone = json.get("phone").toString();
        if (StrUtil.isEmpty(phone)) {
            return ApiResult.ofFailClient("手机号不能为空");
        }
        SysUser currentUser = this.getCurrentUser(request);
        if (StringUtils.isNotEmpty(currentUser.getPhone())) {
            return ApiResult.ofFailClient("手机号已存在，无法再次进行添加操作");
        }
        currentUser.setPhone(phone);
        this.service.updateById(currentUser);
        return ApiResult.ofSuccess(currentUser);
    }

    /**
     * 修改手机号
     */
    @PutMapping("/editPhone")
    public ApiResult editPhone(@RequestBody JsonNode json, HttpServletRequest request) {
        String oldPhone = json.get("oldPhone").toString();
        String newPhone = json.get("newPhone").toString();
        if (StrUtil.isEmpty(oldPhone) || StrUtil.isEmpty(newPhone)) {
            return ApiResult.ofFailClient("手机号不能为空");
        }
        SysUser currentUser = this.getCurrentUser(request);
        if (!currentUser.getPhone().equals(oldPhone)) {
            return ApiResult.ofFailClient("原始手机号输入错误");
        }
        currentUser.setPhone(newPhone);
        this.service.updateById(currentUser);
        return ApiResult.ofSuccess(currentUser);
    }

    /**
     * 校验用户账号是否唯一
     */
    @GetMapping(value = "/checkUsername")
    public ApiResult checkUsername(String id, String username) {
        try {
            SysUser oldUser = null;
            if (StrUtil.isNotEmpty(id)) {
                oldUser = service.getById(id);
            }
            SysUser UserByUsername = service.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUsername, username));
            if (UserByUsername != null) {
                // id 为空 ==> oldUser 为空 => 新增模式 => 只要 UserByUsername 存在 ==> username 已存在 ==> 返回false
                // id 不为空 ==> oldUser 不为空 => 编辑模式 => UserByUsername 存在且与 id 不同 ==> username 已存在 ==> 返回false
                if (oldUser == null || !id.equals(UserByUsername.getId())) {
                    return ApiResult.ofSuccess(false);
                }
            }
            return ApiResult.ofSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ApiResult.ofFailServer("操作失败：" + e.getMessage());
        }
    }


    // 批量冻结
    @PostMapping(value = "/frozenBatch")
    public ApiResult frozenBatch(@RequestParam(name = "ids") String ids, @RequestParam(name = "status") String status) {
        try {
            String[] arr = ids.split(",");
            for (String id : arr) {
                if (StrUtil.isNotEmpty(id)) {
                    SysUser sysUserToUpdate = new SysUser();
                    sysUserToUpdate.setStatus(Integer.parseInt(status));
                    this.service.update(sysUserToUpdate, new UpdateWrapper<SysUser>().lambda().eq(SysUser::getId, id));
                }
            }
            return ApiResult.ofSuccess(null);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ApiResult.ofFailServer("操作失败：" + e.getMessage());
        }
    }


    // 根据 username 或 realname 模糊查找用户列表
    @PostMapping(value = "/getUserListByName")
    public ApiResult listByFuzzyUsernameOrRealName(@RequestBody Map<String, Object> map) {
        try {
            String username = (String) map.get("username");
            List<SysUser> userList = service.listByFuzzyUsernameOrRealName(username);
            return ApiResult.ofSuccess(userList);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ApiResult.ofFailServer("查询失败：" + e.getMessage());
        }
    }

    // 获取所有用户，形成前端下拉框需要的数据
    @GetMapping(value = "/getAllUserForSelect")
    public ApiResult getAllUserForSelect() {
        try {
            List<DictModel> allUserDictModel = service.getAllUserDictModel();
            return ApiResult.ofSuccess(allUserDictModel);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ApiResult.ofFailServer("查询失败：" + e.getMessage());
        }
    }

    // 获取当前登录用户
    @GetMapping(value = "/getSubject")
    public ApiResult getSubject(HttpServletRequest request) {
        try {
            SysUser sysUser = this.getCurrentUser(request);
            return ApiResult.ofSuccess(sysUser);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ApiResult.ofFailServer("查询失败：" + e.getMessage());
        }
    }
}
