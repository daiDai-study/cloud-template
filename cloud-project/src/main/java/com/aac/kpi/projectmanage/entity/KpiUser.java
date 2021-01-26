package com.aac.kpi.projectmanage.entity;

import java.io.Serializable;
import java.util.Date;
import java.sql.Time;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 人员信息表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("kpi_user")
public class KpiUser implements Serializable {

    /**
     * 序列化时保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /**
     * 岗位等级
     */
//    private String grade;

    /**
     * 工号
     */
    private String pernr;

    /**
     * 姓名
     */
    private String sname;

    /**
     * 域账号
     */
    private String usrid;

    /**
     * 岗位
     */
    private String plstx;

    /**
     * 职称
     */
    private String zcms;

    /**
     * 组织
     */
    private String orgtext;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 插入时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date insertTime;

    /**
     *时薪，计算项目成本，单位为万
     */
    private BigDecimal userCost;
}
