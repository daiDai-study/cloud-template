package com.aac.kpi.projectmanage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.aac.kpi.common.constant.CommonConst;
import com.aac.kpi.projectmanage.dto.ProjectTeamDto;
import com.aac.kpi.projectmanage.dto.SysUser;
import com.aac.kpi.projectmanage.entity.KpiProjectTeam;
import com.aac.kpi.projectmanage.entity.KpiRole;
import com.aac.kpi.projectmanage.entity.KpiUser;
import com.aac.kpi.projectmanage.entity.KpiWorkhour;
import com.aac.kpi.projectmanage.fegin.KpiUserFegin;
import com.aac.kpi.projectmanage.fegin.KpiWorkhourFegin;
import com.aac.kpi.projectmanage.mapper.KpiProjectTeamMapper;
import com.aac.kpi.projectmanage.service.IKpiProjectTeamService;
import com.aac.kpi.projectmanage.service.IKpiRoleService;
import com.aac.kpi.projectmanage.service.IKpiUserService;
import com.aac.kpi.projectmanage.service.IKpiWorkhourService;
import com.aac.kpi.projectmanage.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 项目团队信息表
 * @author： xujie
 * @date： 2020-12-21
 * @version： V1.0
 */
@Service
public class KpiProjectTeamServiceImpl extends ServiceImpl<KpiProjectTeamMapper, KpiProjectTeam> implements IKpiProjectTeamService {
    @Resource
    private KpiProjectTeamMapper kpiProjectTeamMapper;
    @Autowired
    private IKpiUserService kpiUserService;
    //        @Autowired
//        private IKpiWorkhourService kpiWorkhourService;
    @Autowired
    private KpiWorkhourFegin kpiWorkhourFegin;
    @Autowired
    private IKpiRoleService kpiRoleService;
    @Autowired
    private KpiUserFegin kpiUserFegin;

    @Override
    public List<KpiProjectTeam> getByProjectNames(List<String> projectNames) {
        QueryWrapper<KpiProjectTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("project", projectNames);
        return this.list(queryWrapper);
    }

    @Override
    public List<KpiProjectTeam> getByProject(String project) {
        QueryWrapper<KpiProjectTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project", project);
        return this.list(queryWrapper);
    }

    @Override
    public List<ProjectTeamDto> getProjectTeamDtoByProject(String project) {
        List<KpiProjectTeam> projectTeams = this.getByProject(project);
        List<ProjectTeamDto> res = new ArrayList<>();
        // 查询成员信息
        List<String> userids = projectTeams.stream().map(KpiProjectTeam::getUsrid).collect(Collectors.toList());
        List<KpiUser> kpiUsers = kpiUserService.getByUserAccount(userids);
        // userId和User对照关系
        Map<String, KpiUser> userIdToUser = kpiUsers.stream().collect(Collectors.toMap(KpiUser::getUsrid, b -> b));

        // role和roleId对照关系
        List<KpiRole> kpiRoles = kpiRoleService.list();
        Map<String, Long> roleToId = kpiRoles.stream().collect(Collectors.toMap(KpiRole::getRole, KpiRole::getId));

        // 工时
        List<String> projects = new ArrayList<>();
        projects.add(project);
        List<KpiWorkhour> workhours = kpiWorkhourFegin.listByProjects(projects);

        Long allHours = 0L;
        if (CollectionUtil.isNotEmpty(workhours)) {
            allHours = workhours.stream().mapToLong(s -> s.getWorkhour() == null ? 0L : s.getWorkhour().longValue()).sum();
        }

        LocalDate now = LocalDate.now();
        String yearMonthInfo = now.toString().substring(0, 7);

        for (KpiProjectTeam projectTeam : projectTeams) {
            ProjectTeamDto dto = new ProjectTeamDto();
            BeanUtil.copyProperties(projectTeam, dto);
            KpiUser kpiUser = userIdToUser.get(projectTeam.getUsrid());
            dto.setUserName(kpiUser.getSname());

            List<KpiWorkhour> userAllWorkHours = workhours.stream().filter(s -> s.getUsrid().equals(projectTeam.getUsrid())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(userAllWorkHours)) {
                long sum = userAllWorkHours.stream().mapToLong(s -> s.getWorkhour() == null ? 0L : s.getWorkhour().longValue()).sum();
                dto.setSumWorkHour(sum);
            } else {
                dto.setSumWorkHour(0L);
            }

            // 当前月工时
            List<KpiWorkhour> currentMonthHours =
                workhours.stream().filter(s ->
                    s.getUsrid().equals(projectTeam.getUsrid())
                        && s.getTheMonth().equals(yearMonthInfo)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(currentMonthHours)) {
                long sum = currentMonthHours.stream().mapToLong(s -> s.getWorkhour() == null ? 0L : s.getWorkhour().longValue()).sum();
                dto.setCurrentMonthHour(sum);
            } else {
                dto.setCurrentMonthHour(0L);
            }

            dto.setRoleId(roleToId.get(dto.getRole()));

            res.add(dto);
        }

        // 计算工时百分比
        if (!allHours.equals(0L)) {
            for (ProjectTeamDto re : res) {
                Float per = re.getSumWorkHour().floatValue() * 100 / allHours;
                re.setHourPercent(per.intValue());
            }
        }

        // 按照角色Id排序
        res.sort(Comparator.comparing(ProjectTeamDto::getRoleId));

        return res;
    }

    @Override
    public String editProjectTeam(ProjectTeamDto projectTeamDto) {
        KpiProjectTeam projectTeam = this.getById(projectTeamDto.getId());
        projectTeam.setRole(projectTeamDto.getRole());
        projectTeam.setBegda(projectTeamDto.getBegda());
        projectTeam.setEndda(projectTeamDto.getEndda());
        projectTeam.setStatus(projectTeamDto.getStatus());
        projectTeam.setUpdateTime(new Date());
        // 获取当前登录用户
        Result<SysUser> subject = kpiUserFegin.getSubject();
        SysUser currentUser = subject.getData();
        projectTeam.setUpdateBy(currentUser.getUsername());
        this.updateById(projectTeam);

        return "1";
    }

    @Override
    public List<KpiProjectTeam> getByUserId(String userId) {
        QueryWrapper<KpiProjectTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("usrid", userId);
        return this.list(queryWrapper);
    }

    @Override
    public List<KpiProjectTeam> getByUserIdAsOwner(String userId) {
        QueryWrapper<KpiProjectTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("usrid", userId);
        String[] roles = CommonConst.PROJECT_OWNER_ROLE_LIST;
        if(roles.length == 1){
            queryWrapper.eq("role", roles[0]);
        }else if(roles.length > 1){
            queryWrapper.in("role", Arrays.asList(roles));
        }
        return this.list(queryWrapper);
    }
}
