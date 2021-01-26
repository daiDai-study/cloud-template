package com.aac.kpi.performance.api.project;

import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ProjectClientFallback implements ProjectClient {

    @Override
    public List<KpiProjectModel> getProjectListWithoutRequiredByUsername(String username) {
        log.error("{} 服务出现异常，进行降级", "project");
        return new ArrayList<>();
    }

    @Override
    public KpiProjectModel getProjectByCode(String project) {
        log.error("{} 服务出现异常，进行降级", "project");
        return null;
    }

    @Override
    public List<KpiProjectTeamModel> getProjectTeamListByProject(String project) {
        log.error("{} 服务出现异常，进行降级", "project");
        return new ArrayList<>();
    }

    @Override
    public List<KpiProjectModel> getProjectListWithoutRequiredByUsernameAsOwner(String username) {
        log.error("{} 服务出现异常，进行降级", "project");
        return new ArrayList<>();
    }

    @Override
    public List<KpiProjectModel> getProjectListWithRequiredByUsername(String username) {
        log.error("{} 服务出现异常，进行降级", "project");
        return new ArrayList<>();
    }

    @Override
    public List<KpiProjectModel> getProjectListByUsernameAsOwner(String username) {
        log.error("{} 服务出现异常，进行降级", "project");
        return new ArrayList<>();
    }
}
