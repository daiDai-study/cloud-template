package com.aac.kpi.system.entity;

import com.aac.kpi.system.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity implements Serializable {

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
    @Dict("sex")
    @JsonSerialize(using= ToStringSerializer.class)
    private Integer sex;
    /**
     * 状态(1：正常  2：冻结 ）
     */
    @Dict("user_status")
    @JsonSerialize(using= ToStringSerializer.class)
    private Integer status;


}
