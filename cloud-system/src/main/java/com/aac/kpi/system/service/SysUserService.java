package com.aac.kpi.system.service;

import com.aac.kpi.system.entity.SysUser;
import com.aac.kpi.common.model.DictModel;
import com.aac.kpi.system.model.vo.UserInfoVO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysUserService extends IService<SysUser> {
    /**
     * 查询用户信息
     */
    IPage<UserInfoVO> queryUserInfoVoWithPage(Page<UserInfoVO> page, UserInfoVO userInfoVO);

    /**
     * 插入用户和用户关联的角色
     */
    void addUserWithRole(UserInfoVO userInfoVO);


    /**
     * 更新用户和用户关联的角色
     */
    void editUserWithRole(UserInfoVO userInfoVO);

    /**
     * 添加用户和用户角色关系
     */
    void addUserWithRole(SysUser user, String... roleIds);

    /**
     * 添加用户角色关系
     */
    void addUserWithRole(String userId, String... roleIds);

    /**
     * 修改用户和用户角色关系
     */
    void editUserWithRole(SysUser user, String... roleIds);

    /**
     * 根据 username 获取用户
     */
    SysUser getByUsername(String username);

    /**
     * 根据 username 查询真实姓名
     */
    List<String> getRealNameByUserName(List<String> usernames);

    /**
     * 根据用户ID和角色Id，获取用户具体信息
     */
    List<SysUser> getUserByIdsAndRoleId(List<String> ids, String roleId);

    List<SysUser> listByRoleCode(String roleCode);

    /**
     * 根据用户名或者域名模糊查找用户列表
     */
    List<SysUser> listByFuzzyUsernameOrRealName(String username);



    List<DictModel> getAllUserDictModel();

    List<JSONObject> queryUsersRoles(String usernames);
}
