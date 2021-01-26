package com.aac.kpi.projectmanage.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface KpiProjectOutService {
    /**
     * 必定不包括必选项目（人人都必填的项目，如日常工作），就算在 project_team 中维护必选项目的成员信息也会排除
     */
    List<JSONObject> getProjectListWithoutRequiredByUsername(String username);
    /**
     * 必定不包括必选项目
     */
    List<JSONObject> getProjectListWithoutRequiredByUsernameAsOwner(String username);
    /**
     * 必定包括必选项目，就算没有在 project_team 中维护必选项目的成员信息也会添加
     */
    List<JSONObject> getProjectListWithRequiredByUsername(String username);
    /**
     * 可能包括必选项目,如果在 project_team 中维护 必选项目的成员信息就会有，否则就没有
     */
    List<JSONObject> getProjectListByUsernameAsOwner(String username);

    JSONObject getProjectByCode(String project);

    List<JSONObject> getProjectTeamListByProject(String project);
}
