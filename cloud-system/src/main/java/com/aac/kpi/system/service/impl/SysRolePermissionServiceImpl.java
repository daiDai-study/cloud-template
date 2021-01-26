package com.aac.kpi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aac.kpi.system.entity.SysRolePermission;
import com.aac.kpi.system.mapper.SysRolePermissionMapper;
import com.aac.kpi.system.service.SysRolePermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {

    @Override
    public void saveRolePermission(String roleId, String permissionIds) {
        LambdaQueryWrapper<SysRolePermission> query = new QueryWrapper<SysRolePermission>().lambda().eq(SysRolePermission::getRoleId, roleId);
        this.remove(query);
        List<SysRolePermission> list = new ArrayList<>();
        String[] arr = permissionIds.split(",");
        for (String p : arr) {
            if (StrUtil.isNotEmpty(p)) {
                SysRolePermission rolePermission = new SysRolePermission(roleId, p);
                list.add(rolePermission);
            }
        }
        this.saveBatch(list);
    }
}
