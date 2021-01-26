package com.aac.kpi.projectmanage.dto;

import com.aac.kpi.projectmanage.entity.KpiProjectTeam;
import lombok.Data;

@Data
public class ProjectTeamDto extends KpiProjectTeam {

    private String userName;
    /**
     * 累计工时
     */
    private Long sumWorkHour;

    /**
     * 工时占比
     */
    private Integer hourPercent = 0;

    /**
     * 当月累计工时
     */
    private Long currentMonthHour;

    /**
     * kpi角色ID，用于排序
     */
    private Long roleId;


}
