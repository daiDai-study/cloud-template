package com.aac.kpi.system.service;

import com.aac.kpi.system.entity.SysRole;
import com.aac.kpi.system.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户角色
 */
public interface SysUserRoleService extends IService<SysUserRole> {

	List<SysUserRole> getByRoleIds(List<String> roleIds);

	List<SysUserRole> getByUserIdsAndRoleId(List<String> userIds, String roleId);

}
