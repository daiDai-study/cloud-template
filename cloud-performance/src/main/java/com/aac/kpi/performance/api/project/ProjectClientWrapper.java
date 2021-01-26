package com.aac.kpi.performance.api.project;

import com.aac.kpi.common.constant.CommonConst;
import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.aac.kpi.performance.util.KpiDateUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ProjectClientWrapper {

    @Resource
    private ProjectClient projectClient;

    public List<KpiProjectModel> listProjectWithoutRequiredByUsername(String username){
        return projectClient.getProjectListWithoutRequiredByUsername(username);
    }

    public List<KpiProjectModel> listProjectWithoutRequiredByUsernameAsOwner(String username){
        return projectClient.getProjectListWithoutRequiredByUsernameAsOwner(username);
    }

    public List<KpiProjectModel> listProjectWithRequiredByUsername(String username){
        return projectClient.getProjectListWithRequiredByUsername(username);
    }

    public List<KpiProjectModel> listProjectByUsernameAsOwner(String username){
        return projectClient.getProjectListByUsernameAsOwner(username);
    }

    public KpiProjectModel getProjectByCode(String project){
        return projectClient.getProjectByCode(project);
    }

    public List<KpiProjectTeamModel> listProjectTeamByProject(String project){
        return projectClient.getProjectTeamListByProject(project);
    }

    public List<KpiProjectModel> listProjectWithoutRequiredByUsernameAndMonth(String username, String theMonth) {
        List<KpiProjectModel> projectListByMonth = this.listProjectWithoutRequiredByUsername(username);

        return filterByMonth(projectListByMonth, theMonth);
    }

    public List<KpiProjectModel> listProjectWithoutRequiredByUsernameAndMonthAsOwner(String username, String theMonth) {
        List<KpiProjectModel> projectListByMonth = this.listProjectWithoutRequiredByUsernameAsOwner(username);

        return filterByMonth(projectListByMonth, theMonth);
    }

    public List<KpiProjectModel> listProjectWithRequiredByUsernameAndMonth(String username, String theMonth) {
        List<KpiProjectModel> projectListByMonth = this.listProjectWithRequiredByUsername(username);

        return filterByMonth(projectListByMonth, theMonth);
    }

    public List<KpiProjectModel> listProjectByUsernameAndMonthAsOwner(String username, String theMonth) {
        List<KpiProjectModel> projectListByMonth = this.listProjectByUsernameAsOwner(username);

        return filterByMonth(projectListByMonth, theMonth);
    }

    private List<KpiProjectModel> filterByMonth(List<KpiProjectModel> projectList, String theMonth){
        // 月份筛选
        return projectList.stream()
            .filter(p -> KpiDateUtil.isBetweenMonth(theMonth, p.getBegda(), p.getEndda()))
            .collect(Collectors.toList());
    }

    public List<KpiProjectTeamModel> getProjectTeamByProjectAndMonthWithoutUsername(String project, String username, String theMonth) {
        List<KpiProjectTeamModel> projectTeamList = this.listProjectTeamByProject(project);

        return projectTeamList.stream()
            .filter(p -> !Objects.equals(p.getUsername(), username))
            .filter(p -> KpiDateUtil.isBetweenMonth(theMonth, p.getBegda(), p.getEndda()))
            .collect(Collectors.toList());
    }

    public List<KpiProjectTeamModel> getProjectTeamByProjectAndMonth(String project, String theMonth) {
        List<KpiProjectTeamModel> projectTeamList = this.listProjectTeamByProject(project);

        return projectTeamList.stream()
            .filter(p -> KpiDateUtil.isBetweenMonth(theMonth, p.getBegda(), p.getEndda()))
            .collect(Collectors.toList());
    }

    /**
     * 判断用户是否为项目成员中的的负责人（见 CommonConst.PROJECT_OWNER_ROLE_LIST）
     * @param kpiProjectTeamModelList 项目成员
     * @param username 指定用户
     * @return 是否为负责人
     */
    public boolean isOwner(List<KpiProjectTeamModel> kpiProjectTeamModelList, String username){
        List<String> projectOwnerRoleList = Arrays.asList(CommonConst.PROJECT_OWNER_ROLE_LIST);
        return kpiProjectTeamModelList.stream().anyMatch(k -> username.equals(k.getUsername()) && projectOwnerRoleList.contains(k.getRole()));
    }
}
