package com.aac.kpi.system.mapper;

import com.aac.kpi.system.entity.SysRole;
import com.aac.kpi.system.entity.SysUser;
import com.aac.kpi.system.model.vo.UserInfoVO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询用户信息
     */
    IPage<UserInfoVO> queryUserInfoVoWithPage(Page<UserInfoVO> page, @Param("userInfoVO") UserInfoVO userInfoVO);

    /**
     * （根据 username）精准查询
     */
    SysUser getByUsername(@Param("username") String username);

    /**
     * （根据 username 或 realname）模糊查询
     */
    List<SysUser> listByFuzzyUsernameOrRealname(@Param("username")String username);

    /**
     * 获取指定角色的所有用户
     */
    List<SysUser> listByRoleCode(String roleCode);



    List<JSONObject> queryUsersRoles(@Param("usernames") String usernames);

    List<SysUser> getUserByIdsAndRoleId(@Param("ids") List<String> ids, @Param("roleId") String roleId);



}
