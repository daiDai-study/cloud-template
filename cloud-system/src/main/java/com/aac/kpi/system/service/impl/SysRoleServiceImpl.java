package com.aac.kpi.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aac.kpi.system.entity.SysRole;
import com.aac.kpi.system.mapper.SysRoleMapper;
import com.aac.kpi.system.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public SysRole getByRoleCode(String role_code) {
        return baseMapper.getByRoleCode(role_code);
    }

    @Override
    public List<SysRole> listByUsername(String username) {
        final List<SysRole> sysRoles = baseMapper.listByUsername(username);
        return CollUtil.defaultIfEmpty(sysRoles, new ArrayList<>());
    }

    @Override
    public List<SysRole> listByUserId(String userId) {
        final List<SysRole> sysRoles = baseMapper.listByUserId(userId);
        return CollUtil.defaultIfEmpty(sysRoles, new ArrayList<>());
    }

    @Override
    public List<String> listNameByUsername(String username) {
        List<SysRole> sysRoles = this.listByUsername(username);
        return sysRoles.stream().map(SysRole::getRoleName).collect(Collectors.toList());
    }

    @Override
    public List<String> listCodeByUsername(String username) {
        List<SysRole> sysRoles = this.listByUsername(username);
        return sysRoles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());
    }
}
