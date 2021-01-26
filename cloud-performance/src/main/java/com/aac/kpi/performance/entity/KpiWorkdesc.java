package com.aac.kpi.performance.entity;

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
public class KpiWorkdesc extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String theMonth;

    private String project;

    private String usrid;

    private String workdesc;

    private Integer poScore;

    private String poRank;
}
