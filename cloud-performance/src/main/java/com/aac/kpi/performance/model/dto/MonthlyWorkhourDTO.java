package com.aac.kpi.performance.model.dto;

import lombok.Data;

@Data
public class MonthlyWorkhourDTO {

    private String project;

    private String theMonth;

    private String username;

    private String recordCount;

    private Float monthlyWorkhourConfirmed;
}
