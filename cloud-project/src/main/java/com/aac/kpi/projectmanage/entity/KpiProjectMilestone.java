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
 * @Description: 项目里程碑事件表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("kpi_project_milestone")
public class KpiProjectMilestone implements Serializable {

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
     * 日期
     */
    private String theDate;

    /**
     * 项目编码
     */
    private String project;

    /**
     * 里程碑
     */
    private String milestone;

    /**
     * 里程碑事件
     */
    private String milestoneEvent;

    /**
     * 删除标记
     */
    @TableLogic
    private Boolean delFlag;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 完成时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    /**
     * 备注
     */
    private String remark;

}
