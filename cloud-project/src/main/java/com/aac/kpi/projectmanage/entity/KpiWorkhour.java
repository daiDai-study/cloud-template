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
 * @Description: 项目工时记录表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@Data
public class KpiWorkhour implements Serializable {

    /**
     * 序列化时保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /**
     * 日期
     */
    private String theDate;

    /**
     * 周次
     */
    private String theWeek;

    /**
     * 月份
     */
    private String theMonth;

    /**
     * 项目编码
     */
    private String project;

    /**
     * 域账号
     */
    private String usrid;

    /**
     * 项目工时
     */
    private BigDecimal workhour;

    /**
     * PO确认的工时
     */
//    private BigDecimal workhourConfirmed = new BigDecimal(0);

    /**
     * 删除标记
     */
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
}
