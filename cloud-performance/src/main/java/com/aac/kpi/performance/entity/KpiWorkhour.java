package com.aac.kpi.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class KpiWorkhour extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String theDate;

    private Integer theWeek;

    private String theMonth;

    private String project;

    private String usrid;

    private BigDecimal workhour;

    /**
     * 确认状态：0-未确认，1-确认通过，-1-确认驳回
     */
    private Integer confirmedStatus;
}
