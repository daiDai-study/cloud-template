package com.aac.kpi.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class KpiCalendar extends BaseEntityWithoutId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String theDate;

    private String dateName;

    private Integer theYear;

    private String yearName;

    private Integer theQuarter;

    private String quarterName;

    private String theMonth;

    private String monthName;

    private Integer theWeek;

    private String weekName;

    private Integer theWeekday;

    private String weekdayName;

    private Boolean isHoliday;

    private String remark;
}
