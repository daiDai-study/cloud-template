package com.aac.kpi.performance.api.project;

import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient(name = "project", path = "/project", fallback = ProjectClientFallback.class)
public interface ProjectClient {

    // 必定不包含必选项目（如日常工作，项目类型为0）
    @GetMapping("/kpiProject/getProjectListWithoutRequiredByUsername")
    List<KpiProjectModel> getProjectListWithoutRequiredByUsername(@RequestParam("username") String username);

    // 必定不包含必选项目（如日常工作，项目类型为0）
    @GetMapping("/kpiProject/getProjectListWithoutRequiredByUsernameAsOwner")
    List<KpiProjectModel> getProjectListWithoutRequiredByUsernameAsOwner(@RequestParam("username") String username);

    // 必定包含必选项目（如日常工作，项目类型为0）
    @GetMapping("/kpiProject/getProjectListWithRequiredByUsername")
    List<KpiProjectModel> getProjectListWithRequiredByUsername(@RequestParam("username") String username);

    // 可能包含必选项目（如日常工作，项目类型为0）
    @GetMapping("/kpiProject/getProjectListByUsernameAsOwner")
    List<KpiProjectModel> getProjectListByUsernameAsOwner(@RequestParam("username") String username);

    @GetMapping("/kpiProject/getProjectByCode")
    KpiProjectModel getProjectByCode(@RequestParam("project")String project);

    @GetMapping("/kpiProject/getProjectTeamListByProject")
    List<KpiProjectTeamModel> getProjectTeamListByProject(@RequestParam("project")String project);
}
