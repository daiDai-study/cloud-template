package com.aac.kpi.performance.service;

import com.aac.kpi.performance.entity.KpiReviewTeam;
import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface KpiReviewTeamService extends IService<KpiReviewTeam> {

    Boolean addOrEditBatch(String currentUsername, List<KpiReviewTeam> kpiReviewTeamList);

    List<KpiReviewTeam> listByUsernameAndMonth(String currentUsername, String theMonth, List<KpiProjectTeamModel> kpiProjectTeamModelList);

    List<KpiReviewTeam> listByProjectAndMonth(String project, String theMonth);

    JsonNode format(List<KpiReviewTeam> list, String currentUsername, String theMonth, List<KpiProjectTeamModel> kpiProjectTeamModelList);
}
