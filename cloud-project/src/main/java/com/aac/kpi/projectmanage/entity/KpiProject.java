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
 * @Description: 项目信息表
 * @author： xujie
 * @date：   2020-12-19
 * @version： V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("kpi_project")
public class KpiProject implements Serializable {

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
     * 项目编码
     */
    private String project;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目价值
     */
    private BigDecimal projectValues;

    /**
     * 项目预算
     */
    private BigDecimal projectBudget;

    /**
     * 项目范围
     */
    private String projectRange;

    /**
     * 项目愿景
     */
    private String projectVision;

    /**
     * 项目开始时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date begda;

    /**
     * 项目结束时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endda;

    /**
     * 删除标记
     */
    @TableLogic
    private Boolean delFlag;

    /**
     * 项目创建时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 项目创建人
     */
    private String createBy;

    /**
     * 项目更新时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 项目更新人
     */
    private String updateBy;

    /**
     *项目类型：0:每个人都会参与的项目，如日常工作，1：ADL项目，2非ADL项目
     */
    private Integer projectType;

    /**
     * 项目状态：1：进行中，2已完成
     */
    private Integer projectStatus;
}
