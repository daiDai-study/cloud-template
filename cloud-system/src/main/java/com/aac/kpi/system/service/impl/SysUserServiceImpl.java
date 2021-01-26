package com.aac.kpi.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aac.kpi.system.entity.SysUser;
import com.aac.kpi.system.entity.SysUserRole;
import com.aac.kpi.system.mapper.SysUserMapper;
import com.aac.kpi.common.model.DictModel;
import com.aac.kpi.system.model.vo.UserInfoVO;
import com.aac.kpi.system.service.SysUserRoleService;
import com.aac.kpi.system.service.SysUserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysUserRoleService sysUserRoleService;

    @Override
    public IPage<UserInfoVO> queryUserInfoVoWithPage(Page<UserInfoVO> page, UserInfoVO userInfoVO) {
        return baseMapper.queryUserInfoVoWithPage(page,userInfoVO);
    }

    @Override
    public void addUserWithRole(UserInfoVO userInfoVO) {
        SysUser sysUser = userInfoVO.buildSysUser();
        baseMapper.insert(sysUser);
        userInfoVO.setId(sysUser.getId());
        List<SysUserRole> sysUserRoles = userInfoVO.buildSysUserRoleList();
        sysUserRoleService.saveBatch(sysUserRoles);
    }

    @Override
    public void editUserWithRole(UserInfoVO userInfoVo) {
        SysUser sysUser = userInfoVo.buildSysUser();
        baseMapper.updateById(sysUser);

        List<SysUserRole> sysUserRoles = userInfoVo.buildSysUserRoleList();
        QueryWrapper<SysUserRole> sysUserRoleQueryWrapper = new QueryWrapper<>();
        sysUserRoleQueryWrapper.eq("user_id",userInfoVo.getId());
        List<SysUserRole> tmp = sysUserRoleService.list(sysUserRoleQueryWrapper);
        sysUserRoleService.remove(sysUserRoleQueryWrapper);
        sysUserRoleService.saveBatch(sysUserRoles);
    }

    @Override
    @Transactional
    public void addUserWithRole(SysUser user, String... roleIds) {
        if (this.save(user)) {
            String userId = user.getId();
            this.addUserWithRole(userId, roleIds);
        }
    }

    @Override
    public void addUserWithRole(String userId, String... roleIds) {
        for (String roleId : roleIds) {
            if (StrUtil.isNotEmpty(roleId)) {
                SysUserRole userRole = new SysUserRole(userId, roleId);
                sysUserRoleService.save(userRole);
            }
        }
    }

    @Override
    public void editUserWithRole(SysUser user, String... roleIds) {
        this.updateById(user);
        // 先删
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, user.getId()));

        // 后加
        String userId = user.getId();
        this.addUserWithRole(userId, roleIds);
    }

    @Override
    public SysUser getByUsername(String username) {
        if(StrUtil.isEmpty(username)){
            return null;
        }
        return baseMapper.getByUsername(username);
    }

    @Override
    public List<JSONObject> queryUsersRoles(String usernames) {
        return baseMapper.queryUsersRoles(usernames);
    }

    @Override
    public List<String> getRealNameByUserName(List<String> usernames) {
        List<String> res = new ArrayList<>();

        if (CollectionUtil.isEmpty(usernames)){
            return res;
        }
        List<SysUser> sysUsers = baseMapper.selectList(new QueryWrapper<SysUser>().in("username", usernames));
        res.addAll(sysUsers.stream().map(SysUser::getRealname).collect(Collectors.toList()));

        return res;
    }

    @Override
    public List<SysUser> getUserByIdsAndRoleId(List<String> ids, String roleId) {
        return baseMapper.getUserByIdsAndRoleId(ids, roleId);
    }

    @Override
    public List<SysUser> listByRoleCode(String roleCode) {
        return baseMapper.listByRoleCode(roleCode);
    }

    @Override
    public List<SysUser> listByFuzzyUsernameOrRealName(String username) {
        return baseMapper.listByFuzzyUsernameOrRealname(username);
    }

    @Override
    public List<DictModel> getAllUserDictModel() {
        List<SysUser> sysUsers = this.list();
        List<DictModel> list = new ArrayList<>();
        for (SysUser sysUser : sysUsers) {
            list.add(new DictModel(sysUser.getId(), sysUser.getRealname()));
        }
        return list;
    }
}