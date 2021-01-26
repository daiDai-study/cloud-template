package com.aac.kpi.performance.service;

import com.aac.kpi.performance.entity.KpiWorkdesc;
import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.aac.kpi.performance.model.vo.KpiTeamMemberMonthlyInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface KpiWorkdescService extends IService<KpiWorkdesc> {

    List<KpiWorkdesc> listByUsernameAndMonth(String currentUsername, String theMonth, List<KpiProjectModel> projectModelList);

    JsonNode format(List<KpiWorkdesc> kpiWorkdescList, String currentUsername, String theMonth, List<KpiProjectModel> projectModelList);

    boolean addOrEditBatch(String currentUsername, List<KpiWorkdesc> kpiWorkdescList);

    List<KpiTeamMemberMonthlyInfoVO> listForScoring(String currentUsername, String theMonth, String project, List<KpiProjectTeamModel> kpiProjectTeamModelList);

    boolean confirmScore(String currentUsername, List<KpiWorkdesc> list);
}
