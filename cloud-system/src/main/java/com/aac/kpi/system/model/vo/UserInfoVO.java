package com.aac.kpi.system.model.vo;

import cn.hutool.core.util.StrUtil;
import com.aac.kpi.system.annotation.Dict;
import com.aac.kpi.system.constant.SystemConst;
import com.aac.kpi.system.entity.SysUser;
import com.aac.kpi.system.entity.SysUserRole;
import com.aac.kpi.system.util.PasswordUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1;

    // 用户信息
    private String id;
    private String username;
    private String realname;
    private String phone;
    private String email;
    @Dict("user_status")
    private Integer status;
    private Boolean delFlag;

    // 角色信息
    private String roleIds;
    private String roleNames;

    // 构建用户
    public SysUser buildSysUser() {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(this.username);
        sysUser.setRealname(this.realname);
        if (this.status != null) {
            sysUser.setStatus(this.status);
        } else {
            sysUser.setStatus(SystemConst.USER_STATUR_NORMAL);
        }
        if (this.phone != null) {
            sysUser.setPhone(this.phone);
        }
        if (this.email != null) {
            sysUser.setEmail(this.email);
        }
        // 必须放在最后，否则没有用户名无法进行加密密码
        if (this.id != null) {
            sysUser.setId(this.id);
        } else {
            // 新增时设置密码
            sysUser.setPassword(PasswordUtil.getEncrypt(sysUser));
        }
        return sysUser;
    }

    // 构建用户角色
    public List<SysUserRole> buildSysUserRoleList() {
        List<SysUserRole> rs = new ArrayList<>();
        if (StrUtil.isNotEmpty(this.roleIds) && StrUtil.isNotEmpty(this.id)) {
            String[] roleIds = this.roleIds.split(",");
            for (String roleId : roleIds) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(roleId);
                sysUserRole.setUserId(this.id);
                rs.add(sysUserRole);
            }
        }
        return rs;
    }
}
