package com.aac.kpi.performance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class KpiProjectTeamModel {

    /**
     * 项目编码
     */
    private String project;

    /**
     * 项目名称
     */
    private String projectName;

    private String role;

    private String roleName;

    private String username;

    private String realname;

    /**
     * 指定用户参与项目开始时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date begda;

    /**
     * 指定用户参与项目结束时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endda;
}
