package com.aac.kpi.projectmanage.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.aac.kpi.projectmanage.entity.KpiProject;
import com.aac.kpi.projectmanage.entity.KpiProjectTeam;
import com.aac.kpi.projectmanage.entity.KpiRole;
import com.aac.kpi.projectmanage.entity.KpiUser;
import com.aac.kpi.projectmanage.service.*;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KpiProjectOutServiceImpl implements KpiProjectOutService {
    @Autowired
    private IKpiProjectService kpiProjectService;
    @Autowired
    private IKpiProjectTeamService kpiProjectTeamService;
    @Autowired
    private IKpiUserService kpiUserService;
    @Autowired
    private IKpiRoleService kpiRoleService;

    @Override
    public List<JSONObject> getProjectListWithoutRequiredByUsername(String username) {
        List<KpiProjectTeam> teams = kpiProjectTeamService.getByUserId(username);
        return getProjectListByProjectTeam(teams, false);
    }

    @Override
    public List<JSONObject> getProjectListWithoutRequiredByUsernameAsOwner(String username) {
        List<KpiProjectTeam> teams = kpiProjectTeamService.getByUserIdAsOwner(username);
        return getProjectListByProjectTeam(teams, false);
    }

    @Override
    public List<JSONObject> getProjectListWithRequiredByUsername(String username) {
        List<KpiProjectTeam> teams = kpiProjectTeamService.getByUserId(username);
        return getProjectListByProjectTeam(teams, true);
    }

    @Override
    public List<JSONObject> getProjectListByUsernameAsOwner(String username) {
        List<KpiProjectTeam> teams = kpiProjectTeamService.getByUserIdAsOwner(username);
        return getProjectListByProjectTeam(teams);
    }

    private List<JSONObject> getProjectListByProjectTeam(List<KpiProjectTeam> teams, boolean required) {
        List<JSONObject> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(teams)) {
            // 所有项目
            List<KpiProject> allProjects = kpiProjectService.list();

            List<KpiProject> requiredProjects = allProjects.stream().filter(k -> Objects.equals(k.getProjectType(), 0)).collect(Collectors.toList());
            List<KpiProject> notRequiredProjects = allProjects.stream().filter(k -> !Objects.equals(k.getProjectType(), 0)).collect(Collectors.toList());

            // 防止添加同一项目
            List<String> projects = new ArrayList<>();

            Map<String, KpiProject> projectToBean = notRequiredProjects.stream().collect(Collectors.toMap(KpiProject::getProject, b -> b));
            for (KpiProjectTeam team : teams) {
                String project = team.getProject();
                KpiProject kpiProject = projectToBean.get(project);
                addProject(list, projects, kpiProject, team);
            }

            // 包含必选项目
            if (required) {
                KpiProjectTeam team = new KpiProjectTeam();
                team.setBegda(DateUtil.date(0L)); // 最小日期
                team.setEndda(DateUtil.parse("2100-12-01")); // 较大日期
                for (KpiProject requiredProject : requiredProjects) {
                    addProject(list, projects, requiredProject, team);
                }
            }

        }
        return list;
    }

    private List<JSONObject> getProjectListByProjectTeam(List<KpiProjectTeam> teams) {
        List<JSONObject> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(teams)) {
            // 所有项目
            List<KpiProject> allProjects = kpiProjectService.list();

            // 防止添加同一项目
            List<String> projects = new ArrayList<>();

            Map<String, KpiProject> projectToBean = allProjects.stream().collect(Collectors.toMap(KpiProject::getProject, b -> b));
            for (KpiProjectTeam team : teams) {
                String project = team.getProject();
                KpiProject kpiProject = projectToBean.get(project);
                addProject(list, projects, kpiProject, team);
            }
        }
        return list;
    }

    private void addProject(List<JSONObject> list, List<String> projects, KpiProject kpiProject, KpiProjectTeam team) {
        if (kpiProject != null) {
            String project = kpiProject.getProject();
            if (projects.contains(project)) {
                return;
            } else {
                projects.add(project);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("project", project);
            jsonObject.put("projectName", kpiProject.getProjectName());
            // 该成员在team的时间
            jsonObject.put("begda", team.getBegda());
            jsonObject.put("endda", team.getEndda());

            list.add(jsonObject);
        }
    }

    @Override
    public JSONObject getProjectByCode(String project) {
        QueryWrapper<KpiProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project", project);
        KpiProject kpiProject = kpiProjectService.getOne(queryWrapper);

        if (kpiProject != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("project", project);
            jsonObject.put("projectName", kpiProject.getProjectName());
            return jsonObject;
        }
        return null;
    }

    @Override
    public List<JSONObject> getProjectTeamListByProject(String project) {
        QueryWrapper<KpiProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project", project);
        KpiProject kpiProject = kpiProjectService.getOne(queryWrapper);

        if (kpiProject == null) {
            return null;
        }

        // 项目成员
        List<KpiProjectTeam> kpiProjectTeams = kpiProjectTeamService.getByProject(project);
        List<String> userIds = kpiProjectTeams.stream().map(KpiProjectTeam::getUsrid).collect(Collectors.toList());
        List<KpiUser> kpiUsers = kpiUserService.getByUserAccount(userIds);
        Map<String, KpiUser> userIdToBean = kpiUsers.stream().collect(Collectors.toMap(KpiUser::getUsrid, b -> b));

        // 角色信息
        List<KpiRole> roles = kpiRoleService.list();
        Map<String, KpiRole> roleToBean = roles.stream().collect(Collectors.toMap(KpiRole::getRole, b -> b));

        List<JSONObject> list = new ArrayList<>();
        for (KpiProjectTeam kpiProjectTeam : kpiProjectTeams) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("project", project);
            jsonObject.put("projectName", kpiProject.getProjectName());
            jsonObject.put("role", kpiProjectTeam.getRole());
            KpiRole kpiRole = roleToBean.get(kpiProjectTeam.getRole());
            jsonObject.put("roleName", kpiRole == null ? "" : kpiRole.getName());
            jsonObject.put("username", kpiProjectTeam.getUsrid());
            KpiUser kpiUser = userIdToBean.get(kpiProjectTeam.getUsrid());
            jsonObject.put("realname", kpiUser == null ? "" : kpiUser.getSname());
            jsonObject.put("begda", kpiProjectTeam.getBegda());
            jsonObject.put("endda", kpiProjectTeam.getEndda());

            list.add(jsonObject);

        }

        return list;
    }
}
