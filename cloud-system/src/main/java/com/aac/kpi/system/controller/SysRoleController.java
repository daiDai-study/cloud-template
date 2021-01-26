package com.aac.kpi.system.controller;


import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.system.entity.SysRole;
import com.aac.kpi.system.service.SysPermissionService;
import com.aac.kpi.system.service.SysRoleService;
import com.aac.kpi.system.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色
 */
@RestController
@RequestMapping("/role")
@Slf4j
public class SysRoleController extends BaseManagementController<SysRole, SysRoleService> {

    @Resource
    public void setS(SysRoleService service){
        this.service = service;
    }

    @Resource
    public void setSysUserService(SysUserService sysUserService){
        this.sysUserService = sysUserService;
    }

    @GetMapping(value = "/list")
    public ApiResult list() {
        List<SysRole> list = service.list();
        if (list == null || list.size() <= 0) {
            return ApiResult.ofFailServer("未找到角色信息");
        }
        return ApiResult.ofSuccess(list);
    }

    @GetMapping(value = "/getById")
    public ApiResult getById(@RequestParam(name = "id", required = true) String id) {
        SysRole sysRole = service.getById(id);
        if (sysRole == null) {
            return ApiResult.ofFailServer("找不到实体类");
        }
        return ApiResult.ofSuccess(sysRole);
    }

    /**
     * 校验角色编码唯一
     * 新增时：id 为空
     * 编辑时：id 有值
     */
    @GetMapping(value = "/checkRoleCode")
    public ApiResult checkRoleCode(String id, String roleCode) {
        try {
            SysRole oldRole = null;
            if (StrUtil.isNotEmpty(id)) {
                oldRole = service.getById(id);
            }
            SysRole roleByRoleCode = service.getOne(new QueryWrapper<SysRole>().lambda().eq(SysRole::getRoleCode, roleCode));
            if (roleByRoleCode != null) {
                // id 为空 ==> oldRole 为空 => 新增模式 => 只要 roleByRoleCode 存在 ==> roleCode 已存在 ==> 返回false
                // id 不为空 ==> oldRole 不为空 => 编辑模式 => roleByRoleCode 存在且与 id 不同 ==> roleCode 已存在 ==> 返回false
                if (oldRole == null || !id.equals(roleByRoleCode.getId())) {
                    return ApiResult.ofSuccess(false);
                }
            }
            return ApiResult.ofSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ApiResult.ofFailServer("操作失败：" + e.getMessage());
        }
    }
}
