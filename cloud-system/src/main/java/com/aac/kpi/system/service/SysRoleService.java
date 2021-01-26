package com.aac.kpi.system.service;

import com.aac.kpi.system.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    /**
     * 根据 role-code 查询角色信息
     */
    SysRole getByRoleCode(String role_code);

    /**
     * 根据 username 查询其角色
     */
    List<SysRole> listByUsername(String username);

    /**
     * 根据 用户Id 查询其角色
     */
    List<SysRole> listByUserId(String userId);

    /**
     * 获取用户的授权角色
     */
    List<String> listNameByUsername(String username);

    /**
     * 获取用户的授权角色编码
     */
    List<String> listCodeByUsername(String username);
}
