package com.aac.kpi.performance.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class KpiTeamMemberMonthlyInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 待确认成员的基础信息
    private String username;

    private String realname;

    private String role;

    private String roleName;

    // 待确认成员的月度工时汇总信息
    private Float workhourForProject;

    private Float workhourForDailyWork;

    // 待确认成员作为被评估人的互评信息
    private List<KpiReviewTeamVO> kpiReviewTeamVOList;

    // 待确认成员作为被评估人的互评综合分数的平均值
    private Integer averageKpi;

    // 月度绩效描述
    private String workdesc;

    // po 确认的分数和等级
    private Integer poScore;

    private String poRank;
}
