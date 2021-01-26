package com.aac.kpi.system.service.impl;

import cn.hutool.core.lang.Assert;
import com.aac.kpi.system.entity.SysUserRole;
import com.aac.kpi.system.mapper.SysUserRoleMapper;
import com.aac.kpi.system.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户角色
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

	@Override
	public List<SysUserRole> getByRoleIds(List<String> roleIds) {
		Assert.notEmpty(roleIds);
		QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
		queryWrapper.in("role_id", roleIds);
		return this.list(queryWrapper);
	}

	@Override
	public List<SysUserRole> getByUserIdsAndRoleId(List<String> userIds, String roleId) {
		Assert.notEmpty(userIds);
		QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
		queryWrapper.in("user_id", userIds).eq("role_id", roleId);
		return this.list(queryWrapper);
	}

}
