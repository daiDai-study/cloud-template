package com.aac.kpi.performance.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class KpiReviewTeamVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String project;

    private String theMonth;

    private String assessee;

    private String assesseeName;

    private String assessor;

    private String asserssorName;

    private Integer kpi1;

    private Integer kpi2;

    private Integer kpi;
}
