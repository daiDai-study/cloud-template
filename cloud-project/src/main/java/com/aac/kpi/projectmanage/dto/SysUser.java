package com.aac.kpi.projectmanage.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class SysUser{

    private static final long serialVersionUID = 1L;

    /**
     * 域账号
     */
    private String username;
    /**
     * 姓名
     */
    private String realname;
    /**
     * 密码
     */
    private String password;
    /**
     * md5密码盐
     */
    private String salt;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 生日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    /**
     * 邮件
     */
    private String email;
    /**
     * 电话
     */
    private String phone;
    /**
     * 性别（1：男 2：女）
     */
    private Integer sex;
    /**
     * 状态(1：正常  2：冻结 ）
     */
    private Integer status;


}
