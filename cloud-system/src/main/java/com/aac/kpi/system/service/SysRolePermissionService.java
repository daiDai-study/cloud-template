package com.aac.kpi.system.service;

import com.aac.kpi.system.entity.SysRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * 角色权限
 */
public interface SysRolePermissionService extends IService<SysRolePermission> {

    /**
     * 保存授权/先删后增
     */
    void saveRolePermission(String roleId, String permissionIds);
}
