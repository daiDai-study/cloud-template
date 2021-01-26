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
 * @Description: 日历表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("kpi_calendar")
public class KpiCalendar implements Serializable {

    /**
     * 序列化时保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using= ToStringSerializer.class)
    private Integer id;

    /**
     * 天
     */
    private String theDate;

    /**
     * 日期
     */
    private String dateName;

    /**
     * 年
     */
    private Integer theYear;

    /**
     * 年份
     */
    private String yearName;

    /**
     * 季
     */
    private Integer theQuarter;

    /**
     * 季度
     */
    private String quarterName;

    /**
     * 月
     */
    private String theMonth;

    /**
     * 月份
     */
    private String monthName;

    /**
     * 周
     */
    private Integer theWeek;

    /**
     * 周次
     */
    private String weekName;

    /**
     * 星期
     */
    private Integer theWeekday;

    /**
     * 星期几
     */
    private String weekdayName;

    /**
     * 是否节假日
     */
    private Boolean isHoliday;

    /**
     * 逻辑删除
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
     * 创建操作人
     */
    private String createUserid;

    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastmodifyTime;

    /**
     * 修改操作人
     */
    private String lastmodifyUserid;

    /**
     * 删除时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;

    /**
     * 删除操作人
     */
    private String deleteUserid;

    /**
     * 备注
     */
    private String remark;
}
