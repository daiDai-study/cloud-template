package com.aac.kpi.projectmanage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.aac.kpi.projectmanage.constant.RoleConstant;
import com.aac.kpi.projectmanage.dto.CreateKpiProjectDto;
import com.aac.kpi.projectmanage.dto.KpiProjectDto;
import com.aac.kpi.projectmanage.dto.ProjectProcessDto;
import com.aac.kpi.projectmanage.dto.SysUser;
import com.aac.kpi.projectmanage.entity.*;
import com.aac.kpi.projectmanage.fegin.KpiUserFegin;
import com.aac.kpi.projectmanage.fegin.KpiWorkhourFegin;
import com.aac.kpi.projectmanage.mapper.KpiProjectMapper;
import com.aac.kpi.projectmanage.service.*;
import com.aac.kpi.projectmanage.util.MyDateUtil;
import com.aac.kpi.projectmanage.util.Result;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 项目信息表
 * @author： xujie
 * @date： 2020-12-19
 * @version： V1.0
 */
@Service
public class KpiProjectServiceImpl extends ServiceImpl<KpiProjectMapper, KpiProject> implements IKpiProjectService {
    @Resource
    private KpiProjectMapper kpiProjectMapper;
    @Autowired
    private IKpiProjectTeamService kpiProjectTeamService;
    @Autowired
    private IKpiUserService kpiUserService;
    @Autowired
    private IKpiProjectMilestoneService kpiProjectMilestoneService;
    @Autowired
    private KpiWorkhourFegin kpiWorkhourFegin;
    @Autowired
    private KpiUserFegin kpiUserFegin;
    @Autowired
    private IKpiProjectValueService kpiProjectValueService;

    @Override
    public List<KpiProjectDto> getKpiProjectByStatus(Integer status) {
        // status表示取出进行中的项目，1表示取出已完成的项目
        List<KpiProject> allProject = CollUtil.defaultIfEmpty(this.list(), new ArrayList<>());
        // 排除掉 project_type = 0 的项目
        allProject = allProject.stream().filter(k -> !Objects.equals(k.getProjectType(), 0)).collect(Collectors.toList());
        Date now = new Date();
        List<KpiProject> filterResult = allProject.stream().filter(s ->
            s.getProjectStatus().equals(status)
        ).collect(Collectors.toList());

        List<KpiProjectDto> res = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(filterResult)) {
            for (KpiProject kpiProject : filterResult) {
                KpiProjectDto dto = new KpiProjectDto();
                BeanUtil.copyProperties(kpiProject, dto);
                res.add(dto);
            }
        }

        // 设置其它属性
        if (CollectionUtil.isNotEmpty(res)) {
            List<String> projectNames = res.stream().map(KpiProject::getProject).collect(Collectors.toList());
            List<KpiProjectTeam> projectTeams = kpiProjectTeamService.getByProjectNames(projectNames);
            List<String> userAccounts = projectTeams.stream().map(KpiProjectTeam::getUsrid).collect(Collectors.toList());
            List<KpiUser> userAccount = kpiUserService.getByUserAccount(userAccounts);
            // 一次性查出用户工时
            List<KpiWorkhour> workhours = kpiWorkhourFegin.listByProjects(projectNames);

            for (KpiProjectDto re : res) {
                // 筛选出项目成员
                List<KpiProjectTeam> filterProjectTeams = projectTeams.stream().filter(s -> s.getProject().equals(re.getProject())).collect(Collectors.toList());
                re.setProjectUserNum(filterProjectTeams.size());

                // 计算项目从开始到今天花了多久
                Long projectUseDay = (now.getTime() - re.getBegda().getTime()) / (24 * 3600 * 1000);
                re.setProjectUseDay(projectUseDay.intValue());

                // 计算项目花了多少成本
                List<String> thisProjectUserAccounts = filterProjectTeams.stream().map(KpiProjectTeam::getUsrid).collect(Collectors.toList());
                List<KpiUser> filterKpiUsers = userAccount.stream().filter(s -> thisProjectUserAccounts.contains(s.getUsrid())).collect(Collectors.toList());
                List<KpiWorkhour> filterWorkHours = workhours.stream().filter(s -> s.getProject().equals(re.getProject())).collect(Collectors.toList());
                // 汇总项目成本
                Float projectCost = 0F;
                if (CollectionUtil.isNotEmpty(filterKpiUsers) && CollectionUtil.isNotEmpty(filterWorkHours)) {
                    for (KpiUser filterKpiUser : filterKpiUsers) {
                        BigDecimal userCost = filterKpiUser.getUserCost();
                        if (userCost == null) {
                            userCost = new BigDecimal(0);
                        }
                        List<KpiWorkhour> singleUserWorkHour = filterWorkHours.stream().filter(s -> s.getUsrid().equals(filterKpiUser.getUsrid())).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(singleUserWorkHour)) {
                            Integer sumWorkHour = singleUserWorkHour.stream().mapToInt(s -> s.getWorkhour() == null ? 0 : s.getWorkhour().intValue()).sum();
                            projectCost += userCost.floatValue() * sumWorkHour;
                        }
                    }
                }
                re.setProjectCost(projectCost);

                // 项目用户角色信息
                if (CollectionUtil.isNotEmpty(filterProjectTeams)) {
                    Map<String, KpiUser> userIdToUser = filterKpiUsers.stream().collect(Collectors.toMap(KpiUser::getUsrid, b -> b));
                    List<String> userRoleInfo = new ArrayList<>();
                    Set<KpiProjectTeam> set = new HashSet<>(filterProjectTeams);
                    // 排序，PO，BT，SM，ARCH，其余随意
                    Map<String, List<KpiProjectTeam>> roleToTeams = new HashMap<>();
                    for (KpiProjectTeam kpiProjectTeam : set) {
                        if (roleToTeams.get(kpiProjectTeam.getRole()) == null) {
                            List<KpiProjectTeam> list = new ArrayList<>();
                            list.add(kpiProjectTeam);
                            roleToTeams.put(kpiProjectTeam.getRole(), list);
                        } else {
                            roleToTeams.get(kpiProjectTeam.getRole()).add(kpiProjectTeam);
                        }
                    }
                    List<KpiProjectTeam> sortedTeams = new ArrayList<>();

                    if (CollectionUtil.isNotEmpty(roleToTeams.get(RoleConstant.ROLE_PO))) {
                        sortedTeams.addAll(roleToTeams.get(RoleConstant.ROLE_PO));
                    }
                    if (CollectionUtil.isNotEmpty(roleToTeams.get(RoleConstant.ROLE_BT))) {
                        sortedTeams.addAll(roleToTeams.get(RoleConstant.ROLE_BT));
                    }
                    if (CollectionUtil.isNotEmpty(roleToTeams.get(RoleConstant.ROLE_SM))) {
                        sortedTeams.addAll(roleToTeams.get(RoleConstant.ROLE_SM));
                    }
                    if (CollectionUtil.isNotEmpty(roleToTeams.get(RoleConstant.ROLE_ARCH))) {
                        sortedTeams.addAll(roleToTeams.get(RoleConstant.ROLE_ARCH));
                    }
                    // 将剩余的塞到sortedTeams
                    for (Map.Entry<String, List<KpiProjectTeam>> stringListEntry : roleToTeams.entrySet()) {
                        String key = stringListEntry.getKey();
                        List<KpiProjectTeam> value = stringListEntry.getValue();
                        if (!key.equals(RoleConstant.ROLE_PO) && !key.equals(RoleConstant.ROLE_BT) && !key.equals(RoleConstant.ROLE_SM) && !key.equals(RoleConstant.ROLE_ARCH)) {
                            sortedTeams.addAll(value);
                        }
                    }

                    for (KpiProjectTeam kpiProjectTeam : sortedTeams) {
                        String info = kpiProjectTeam.getRole() + " " +
                            (userIdToUser.get(kpiProjectTeam.getUsrid()) == null ?
                                kpiProjectTeam.getUsrid() :
                                userIdToUser.get(kpiProjectTeam.getUsrid()).getSname());
                        userRoleInfo.add(info);
                    }
                    re.setUserRoleInfo(userRoleInfo);
                }

                // 组装柱状图的数据
                // 1：先取出项目里面有几种角色
                // 2：每个角色在最近7天所消耗的成本
                // 将项目成员根据角色分组
                Map<String, List<KpiProjectTeam>> roleToProjectTeams = new HashMap<>();
                for (KpiProjectTeam filterProjectTeam : filterProjectTeams) {
                    String role = filterProjectTeam.getRole();
                    if (roleToProjectTeams.get(role) == null) {
                        List<KpiProjectTeam> list = new ArrayList<>();
                        list.add(filterProjectTeam);
                        roleToProjectTeams.put(role, list);
                    } else {
                        roleToProjectTeams.get(role).add(filterProjectTeam);
                    }
                }
                // filterKpiUsers转换为userId和时薪的对照关系
                Map<String, BigDecimal> userIdToCost = filterKpiUsers.stream().collect(Collectors.toMap(KpiUser::getUsrid, KpiUser::getUserCost));


                LocalDate nowLocalDate = LocalDate.now();
                // 查看项目开始了几天，如果大于等于七天，柱状图显示7天数据，否则有几天显示几天
                List<Integer> dayCategories = new ArrayList<>();
                // 如果项目开始大于了七天，各个角色有七天前成本汇总初始值
                Map<String, Float> initRoleToCost = new HashMap<>();
                if (projectUseDay > 7) {
                    for (int i = 6; i >= 0; i--) {
                        dayCategories.add(projectUseDay.intValue() - i);
                    }
                    // 计算下每个角色在七天前的成本汇总
                    LocalDate sevenDayAgo = nowLocalDate.minusDays(7);
                    for (Map.Entry<String, List<KpiProjectTeam>> stringListEntry : roleToProjectTeams.entrySet()) {
                        String role = stringListEntry.getKey();
                        List<KpiProjectTeam> value = stringListEntry.getValue();
                        Float roleCostSum = 0F;
                        for (KpiProjectTeam kpiProjectTeam : value) {
                            // 找出这个人这天的工时，再乘以时薪
                            BigDecimal userCost = userIdToCost.get(kpiProjectTeam.getUsrid());
                            // 这个人，并且时间小于7天前
                            List<KpiWorkhour> collect = filterWorkHours.stream().filter(s -> s.getUsrid().equals(kpiProjectTeam.getUsrid())
                                && s.getTheDate().compareTo(sevenDayAgo.toString()) < 0).collect(Collectors.toList());
                            if (CollectionUtil.isNotEmpty(collect)) {
                                int workHours = collect.stream().mapToInt(s -> s.getWorkhour() == null ? 0 : s.getWorkhour().intValue()).sum();
                                roleCostSum += (userCost.floatValue() * workHours);
                            }
                        }
                        initRoleToCost.put(role, roleCostSum);
                    }

                } else {
                    for (int i = projectUseDay.intValue() - 1; i >= 0; i--) {
                        dayCategories.add(projectUseDay.intValue() - i);
                    }
                }
                re.setDayCategories(dayCategories);


                List<KpiProjectDto.ColumnUserData> columnUserDatas = new ArrayList<>();
                for (Map.Entry<String, List<KpiProjectTeam>> stringListEntry : roleToProjectTeams.entrySet()) {
                    String role = stringListEntry.getKey();
                    List<KpiProjectTeam> value = stringListEntry.getValue();
                    KpiProjectDto.ColumnUserData singleData = new KpiProjectDto.ColumnUserData();
                    singleData.setName(role);
                    List<Float> data = new ArrayList<>();
                    for (int i = dayCategories.size(); i > 0; i--) {
                        // 这个角色初始值，应该是上一天的值，这样累加下去
                        Float previousValue = data.size() > 0 ? data.get(data.size() - 1)
                            : (initRoleToCost.get(role) != null ? initRoleToCost.get(role) : 0F);
                        Float roleDayCost = new Float(previousValue);
                        // 往前推
                        LocalDate minusDays = nowLocalDate.minusDays(i);
                        for (KpiProjectTeam kpiProjectTeam : value) {
                            // 找出这个人这天的工时，再乘以时薪
                            BigDecimal userCost = userIdToCost.get(kpiProjectTeam.getUsrid());
                            if (userCost == null) {
                                userCost = new BigDecimal(0);
                            }
                            Optional<KpiWorkhour> first = filterWorkHours.stream().filter(s ->
                                s.getUsrid().equals(kpiProjectTeam.getUsrid())
                                    && s.getTheDate().equals(minusDays.toString())).findFirst();
                            if (first.isPresent()) {

                                int hour = first.get().getWorkhour() == null ?
                                    0 : first.get().getWorkhour().intValue();
                                roleDayCost += userCost.floatValue() * hour;
                            }
                        }
                        data.add(roleDayCost);
                    }
                    singleData.setData(data);
                    columnUserDatas.add(singleData);
                }
                re.setColumnUserDatas(columnUserDatas);

                // 计算每个柱子的项目成本
//                                Float initCostSum = 0F;
//                                if (projectUseDay > 7) {
//                                        // 如果项目已经开始大于7天了，那么先得汇总七天前的数据
//                                        LocalDate sevenDayAgo = nowLocalDate.minusDays(7);
//                                        List<KpiWorkhour> sevenDayAgoHours =
//                                                filterWorkHours.stream().filter(s -> s.getTheDate().compareTo(sevenDayAgo.toString()) < 0).collect(Collectors.toList());
//                                        if (CollectionUtil.isNotEmpty(sevenDayAgoHours)) {
//                                                for (KpiUser filterKpiUser : filterKpiUsers) {
//                                                        BigDecimal userCost = filterKpiUser.getUserCost();
//                                                        if (userCost == null) {
//                                                                userCost = new BigDecimal(0);
//                                                        }
//                                                        List<KpiWorkhour> singleUserWorkHour = sevenDayAgoHours.stream().filter(s -> s.getUsrid().equals(filterKpiUser.getUsrid())).collect(Collectors.toList());
//                                                        if (CollectionUtil.isNotEmpty(singleUserWorkHour)) {
//                                                                Integer sumWorkHour = singleUserWorkHour.stream().mapToInt(s->s.getWorkhour().intValue()).sum();
//                                                                initCostSum += userCost.floatValue()*sumWorkHour;
//                                                        }
//                                                }
//                                        }
//                                }
                // 开始汇总
                List<Float> columnCostSum = new ArrayList<>();
                for (int i = 0; i < dayCategories.size(); i++) {
                    Float initCostSum = 0F;
                    for (KpiProjectDto.ColumnUserData columnUserData : columnUserDatas) {
                        List<Float> data = columnUserData.getData();
                        initCostSum += data.get(i);
                    }
                    Float setValue = new Float(initCostSum);
                    columnCostSum.add(setValue);
                }
                re.setColumnCostSum(columnCostSum);

            }
        }

        return res;
    }

    @Override
    @Transactional
    public String createProject(CreateKpiProjectDto createKpiProjectDto) {
        KpiProject kpiProject = new KpiProject();
        BeanUtil.copyProperties(createKpiProjectDto, kpiProject);
        // 这边需要调用用户微服务，从而确定当前登录用户是谁
        Result<SysUser> subject = kpiUserFegin.getSubject();
        SysUser currentUser = subject.getData();
        kpiProject.setCreateTime(new Date());
        kpiProject.setUpdateTime(new Date());
        kpiProject.setCreateBy(currentUser.getUsername());
        kpiProject.setUpdateBy(currentUser.getUsername());
        this.save(kpiProject);

        // 将团队成员保存到project_team表
        List<CreateKpiProjectDto.RoleUser> roleUsers = createKpiProjectDto.getRoleUsers();
        for (CreateKpiProjectDto.RoleUser roleUser : roleUsers) {
            String role = roleUser.getRole();
            List<String> users = roleUser.getUsers();
            if (CollectionUtil.isNotEmpty(users) && StringUtils.isNotEmpty(role)) {
                for (String user : users) {
                    this.createProjectTeam(createKpiProjectDto, role, user, currentUser.getUsername());
                }
            }
        }


//                if (CollectionUtil.isNotEmpty(createKpiProjectDto.getArchs())) {
//                        List<String> archs = createKpiProjectDto.getArchs();
//                        for (String arch : archs) {
//                                this.createProjectTeam(createKpiProjectDto, RoleConstant.ROLE_ARCH, arch, currentUser);
//                        }
//                }
//                if (CollectionUtil.isNotEmpty(createKpiProjectDto.getSms())) {
//                        for (String sm : createKpiProjectDto.getSms()) {
//                                this.createProjectTeam(createKpiProjectDto, RoleConstant.ROLE_SM, sm, currentUser);
//                        }
//                }
//                if (CollectionUtil.isNotEmpty(createKpiProjectDto.getPos())) {
//                        for (String po : createKpiProjectDto.getPos()) {
//                                this.createProjectTeam(createKpiProjectDto, RoleConstant.ROLE_PO, po, currentUser);
//                        }
//                }
//                if (CollectionUtil.isNotEmpty(createKpiProjectDto.getBts())) {
//                        for (String bt : createKpiProjectDto.getBts()) {
//                                this.createProjectTeam(createKpiProjectDto, RoleConstant.ROLE_BT, bt, currentUser);
//                        }
//                }
//                if (CollectionUtil.isNotEmpty(createKpiProjectDto.getUis())) {
//                        for (String ui : createKpiProjectDto.getUis()) {
//                                this.createProjectTeam(createKpiProjectDto, RoleConstant.ROLE_UI, ui, currentUser);
//                        }
//                }
//                if (CollectionUtil.isNotEmpty(createKpiProjectDto.getDes())) {
//                        for (String de : createKpiProjectDto.getDes()) {
//                                this.createProjectTeam(createKpiProjectDto, RoleConstant.ROLE_DE, de, currentUser);
//                        }
//                }
//                if (CollectionUtil.isNotEmpty(createKpiProjectDto.getDevs())) {
//                        for (String dev : createKpiProjectDto.getDevs()) {
//                                this.createProjectTeam(createKpiProjectDto, RoleConstant.ROLE_DEV, dev, currentUser);
//                        }
//                }

        // 需要在里程碑事件里面新增记录
        KpiProjectMilestone kpiProjectMilestone = new KpiProjectMilestone();
        kpiProjectMilestone.setProject(kpiProject.getProject());
        String timeString = LocalDateTimeUtil.of(kpiProject.getBegda()).toLocalDate().toString();
        kpiProjectMilestone.setTheDate(timeString);
        kpiProjectMilestone.setMilestone("创建");

        kpiProjectMilestone.setCreateTime(new Date());
        kpiProjectMilestone.setUpdateTime(new Date());
        kpiProjectMilestone.setCreateBy(currentUser.getUsername());
        kpiProjectMilestone.setUpdateBy(currentUser.getUsername());

        kpiProjectMilestoneService.save(kpiProjectMilestone);

        return "1";
    }

    private void createProjectTeam(CreateKpiProjectDto createKpiProjectDto, String role,
                                   String userId, String currentUserId) {
        KpiProjectTeam kpiProjectTeam = new KpiProjectTeam();
        kpiProjectTeam.setProject(createKpiProjectDto.getProject());
        kpiProjectTeam.setRole(role);
        kpiProjectTeam.setUsrid(userId);
        kpiProjectTeam.setBegda(createKpiProjectDto.getBegda());
        kpiProjectTeam.setEndda(createKpiProjectDto.getEndda());
        kpiProjectTeam.setCreateTime(new Date());
        kpiProjectTeam.setUpdateTime(new Date());
        kpiProjectTeam.setCreateBy(currentUserId);
        kpiProjectTeam.setUpdateBy(currentUserId);
        kpiProjectTeam.setStatus(1);
        kpiProjectTeamService.save(kpiProjectTeam);
    }

    @Override
    public void deleteProjectById(Long id) {
        KpiProject kpiProject = this.getById(id);
        // 将项目下的成员也删除
        List<KpiProjectTeam> projectTeams = kpiProjectTeamService.getByProject(kpiProject.getProject());
        if (CollectionUtil.isNotEmpty(projectTeams)) {
            List<Long> ids = projectTeams.stream().map(KpiProjectTeam::getId).collect(Collectors.toList());
            kpiProjectTeamService.removeByIds(ids);
        }

        this.removeById(id);
    }

    @Override
    public CreateKpiProjectDto getEditProjectInfo(Long id) {
        CreateKpiProjectDto createKpiProjectDto = new CreateKpiProjectDto();
        KpiProject kpiProject = this.getById(id);
        BeanUtil.copyProperties(kpiProject, createKpiProjectDto);

        // 查询人员信息
        List<KpiProjectTeam> teams = kpiProjectTeamService.getByProject(kpiProject.getProject());

        if (CollectionUtil.isNotEmpty(teams)) {
            // 按照角色分组
            Map<String, List<KpiProjectTeam>> map = new HashMap<>();
            for (KpiProjectTeam team : teams) {
                if (map.get(team.getRole()) == null) {
                    List<KpiProjectTeam> list = new ArrayList<>();
                    list.add(team);
                    map.put(team.getRole(), list);
                } else {
                    map.get(team.getRole()).add(team);
                }
            }

            List<CreateKpiProjectDto.RoleUser> roleUsers = new ArrayList<>();
            Integer roleUserId = 1;
            for (Map.Entry<String, List<KpiProjectTeam>> stringListEntry : map.entrySet()) {
                String role = stringListEntry.getKey();
                List<KpiProjectTeam> value = stringListEntry.getValue();
                List<String> collect = value.stream().map(s -> s.getUsrid()).collect(Collectors.toList());
                CreateKpiProjectDto.RoleUser roleUser = new CreateKpiProjectDto.RoleUser();
                roleUser.setRole(role);
                roleUser.setUsers(collect);
                roleUser.setId(roleUserId);
                roleUsers.add(roleUser);
                roleUserId++;
            }
            createKpiProjectDto.setRoleUsers(roleUsers);


        }
        return createKpiProjectDto;
    }

    @Override
    @Transactional
    public String saveEditProject(CreateKpiProjectDto createKpiProjectDto) {
        KpiProject kpiProject = this.getById(createKpiProjectDto.getId());
        // 设置基础属性
        kpiProject.setProject(createKpiProjectDto.getProject());
        kpiProject.setProjectName(createKpiProjectDto.getProjectName());
        kpiProject.setBegda(createKpiProjectDto.getBegda());
        kpiProject.setEndda(createKpiProjectDto.getEndda());
        kpiProject.setProjectBudget(createKpiProjectDto.getProjectBudget());
        kpiProject.setProjectRange(createKpiProjectDto.getProjectRange());
        kpiProject.setProjectValues(createKpiProjectDto.getProjectValues());
        kpiProject.setProjectVision(createKpiProjectDto.getProjectVision());
        kpiProject.setUpdateTime(new Date());
        kpiProject.setProjectType(createKpiProjectDto.getProjectType());
        kpiProject.setProjectStatus(createKpiProjectDto.getProjectStatus());
        // 从用户微服务获取当前登录用户信息
        Result<SysUser> subject = kpiUserFegin.getSubject();
        SysUser currentUser = subject.getData();
        kpiProject.setUpdateBy(currentUser.getUsername());
        this.updateById(kpiProject);

        // 更新人员信息
        List<KpiProjectTeam> projectTeams = kpiProjectTeamService.getByProject(kpiProject.getProject());
        // 按照角色分组
//                Map<String, List<KpiProjectTeam>> roleToTeams = new HashMap<>();
//                for (KpiProjectTeam projectTeam : projectTeams) {
//                        List<KpiProjectTeam> list = roleToTeams.get(projectTeam.getRole());
//                        if (list == null) {
//                                list = new ArrayList<>();
//                        }
//                        list.add(projectTeam);
//                        roleToTeams.put(projectTeam.getRole(), list);
//                }

        // 查看编辑的人员信息
        // 获取当前登录用户
        // 将原来的人删除，更新为编辑的人
        List<Long> teamIds = projectTeams.stream().map(s -> s.getId()).collect(Collectors.toList());
        kpiProjectTeamService.removeByIds(teamIds);

        // 将团队成员保存到project_team表
        List<CreateKpiProjectDto.RoleUser> roleUsers = createKpiProjectDto.getRoleUsers();
        for (CreateKpiProjectDto.RoleUser roleUser : roleUsers) {
            String role = roleUser.getRole();
            List<String> users = roleUser.getUsers();
            if (CollectionUtil.isNotEmpty(users) && StringUtils.isNotEmpty(role)) {
                for (String user : users) {
                    this.createProjectTeam(createKpiProjectDto, role, user, currentUser.getUsername());
                }
            }
        }


        return "1";
    }


    private void checkOldAndNewTeam(List<KpiProjectTeam> oldTeam, List<String> newTeam,
                                    CreateKpiProjectDto createKpiProjectDto, String role,
                                    String currentUser) {
        if (CollectionUtil.isEmpty(oldTeam)) {
            if (CollectionUtil.isNotEmpty(newTeam)) {
                // 新增
                for (String s : newTeam) {
                    this.createProjectTeam(createKpiProjectDto, role, s, currentUser);
                }
            }
        } else {
            // oldTeam不为空的时候
            // 查出所有oldTeam不在newTeam里面的人，这些人删除
            List<KpiProjectTeam> notInNewUser = oldTeam.stream().filter(s -> !newTeam.contains(s.getUsrid())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(notInNewUser)) {
                List<Long> delIds = notInNewUser.stream().map(KpiProjectTeam::getId).collect(Collectors.toList());
                kpiProjectTeamService.removeByIds(delIds);
            }
            // 查出所有newTeam里面有，但是oldTeam里面没有的，新增
            List<String> allOldTeamUsers = oldTeam.stream().map(KpiProjectTeam::getUsrid).collect(Collectors.toList());
            List<String> createTeams = newTeam.stream().filter(s -> !allOldTeamUsers.contains(s)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(createTeams)) {
                for (String createTeam : createTeams) {
                    this.createProjectTeam(createKpiProjectDto, role, createTeam, currentUser);
                }
            }

        }
    }

    @Override
    public KpiProjectDto getProjectDetailInfoById(Long id) {
        KpiProjectDto kpiProjectDto = new KpiProjectDto();
        KpiProject kpiProject = this.getById(id);
        BeanUtil.copyProperties(kpiProject, kpiProjectDto);

//                Date now = new Date();
//                if (kpiProject.getEndda() != null && kpiProject.getEndda().compareTo(now) > 0) {
//                        kpiProjectDto.setProjectStatus("进行中");
//                } else {
//                        kpiProjectDto.setProjectStatus("已结束");
//                }

        // 计算项目时长
        // 筛选出当月的数据
        LocalDate nowLocalDate = LocalDate.now();
        String yearMonth = nowLocalDate.toString().substring(0, 7);
        List<String> projects = new ArrayList<>();
        projects.add(kpiProject.getProject());
        List<KpiWorkhour> workHours = kpiWorkhourFegin.listByProjects(projects);
        if (CollectionUtil.isNotEmpty(workHours)) {
            int allWorkHour = workHours.stream().mapToInt(s -> s.getWorkhour() == null ? 0 : s.getWorkhour().intValue()).sum();
            kpiProjectDto.setSumProjectHour(allWorkHour);

            List<KpiWorkhour> filterCurrentMonthData = workHours.stream().filter(s -> s.getTheDate().substring(0, 7).equals(yearMonth)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(filterCurrentMonthData)) {
                int currentMonthsumHours = filterCurrentMonthData.stream().mapToInt(s -> s.getWorkhour() == null ? 0 : s.getWorkhour().intValue()).sum();
                kpiProjectDto.setCurrentMonthHour(currentMonthsumHours);
            }
        }

        // 成员数
        List<KpiProjectTeam> projectTeams = kpiProjectTeamService.getByProject(kpiProject.getProject());
        kpiProjectDto.setProjectUserNum(CollectionUtil.isEmpty(projectTeams) ? 0 : projectTeams.size());

        if (CollectionUtil.isNotEmpty(projectTeams)) {
            List<String> userids = projectTeams.stream().map(KpiProjectTeam::getUsrid).collect(Collectors.toList());
            List<KpiUser> kpiUsers = kpiUserService.getByUserAccount(userids);
            // 汇总项目成本
            Float projectCost = 0F;
            Float currentMonthProjectCost = 0F;
            if (CollectionUtil.isNotEmpty(kpiUsers) && CollectionUtil.isNotEmpty(workHours)) {
                for (KpiUser filterKpiUser : kpiUsers) {
                    BigDecimal userCost = filterKpiUser.getUserCost();
                    if (userCost == null) {
                        userCost = new BigDecimal(0);
                    }
                    List<KpiWorkhour> singleUserWorkHour = workHours.stream().filter(s -> s.getUsrid().equals(filterKpiUser.getUsrid())).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(singleUserWorkHour)) {
                        Integer sumWorkHour = singleUserWorkHour.stream().mapToInt(s -> s.getWorkhour() == null ? 0 : s.getWorkhour().intValue()).sum();
                        projectCost += userCost.floatValue() * sumWorkHour;
                        // 过滤下当月的数据
                        List<KpiWorkhour> currentMonthUserHours = singleUserWorkHour.stream().filter(s -> s.getTheDate().substring(0, 7).equals(yearMonth)).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(currentMonthUserHours)) {
                            Integer currentSumWorkHour = currentMonthUserHours.stream().mapToInt(s -> s.getWorkhour() == null ? 0 : s.getWorkhour().intValue()).sum();
                            currentMonthProjectCost += userCost.floatValue() * currentSumWorkHour;
                        }
                    }
                }
            }
            kpiProjectDto.setProjectCost(projectCost);
            kpiProjectDto.setCurrentMonthProjectCost(currentMonthProjectCost);
        }

        List<KpiProjectValue> values = kpiProjectValueService.getProjectValueByProjectId(id);
        if (CollectionUtil.isNotEmpty(values)) {
            long sumValue = values.stream().mapToLong(s -> s.getValue() == null?0L:s.getValue()).sum();
            kpiProjectDto.setSumProjectValue(sumValue);
        }


        return kpiProjectDto;
    }

    @Override
    public KpiProjectDto getProjectCostData(Long id, String start, String end, Integer type) {
        KpiProjectDto kpiProjectDto = new KpiProjectDto();
        // type:1按日统计，2按周统计，3按月统计
        KpiProject kpiProject = this.getById(id);

        Date begda = kpiProject.getBegda();
        LocalDate projectStartDate = LocalDateTimeUtil.of(begda).toLocalDate();

        // 项目成员
        List<KpiProjectTeam> kpiProjectTeams = kpiProjectTeamService.getByProject(kpiProject.getProject());
        // 项目工时
        List<String> projects = new ArrayList<>();
        projects.add(kpiProject.getProject());
        List<KpiWorkhour> workhours = kpiWorkhourFegin.listByProjects(projects);

        String startYear = start.substring(0, 4);
        String startMonth = start.substring(5, 7);
        String startDay = start.substring(8, 10);
        LocalDate startDate = LocalDate.of(Integer.valueOf(startYear), Integer.valueOf(startMonth), Integer.valueOf(startDay));
        String endYear = end.substring(0, 4);
        String endMonth = end.substring(5, 7);
        String endDay = end.substring(8, 10);
        LocalDate endDate = LocalDate.of(Integer.valueOf(endYear), Integer.valueOf(endMonth), Integer.valueOf(endDay));


        if (CollectionUtil.isNotEmpty(kpiProjectTeams) && CollectionUtil.isNotEmpty(workhours)) {
            // 查出小组成员信息
            // 项目人员信息
            List<String> userids = kpiProjectTeams.stream().map(KpiProjectTeam::getUsrid).collect(Collectors.toList());
            List<KpiUser> kpiUsers = kpiUserService.getByUserAccount(userids);
            Map<String, BigDecimal> userIdToCost = kpiUsers.stream().collect(Collectors.toMap(KpiUser::getUsrid, KpiUser::getUserCost));
            // 按照角色分组
            Map<String, List<KpiProjectTeam>> roleToProjectTeams = new HashMap<>();
            for (KpiProjectTeam projectTeam : kpiProjectTeams) {
                String role = projectTeam.getRole();
                if (roleToProjectTeams.get(role) == null) {
                    List<KpiProjectTeam> list = new ArrayList<>();
                    list.add(projectTeam);
                    roleToProjectTeams.put(role, list);
                } else {
                    roleToProjectTeams.get(role).add(projectTeam);
                }
            }
            // 汇总每个角色start之前的成本
            Map<String, Float> initRoleToCost = new HashMap<>();
            // 计算下每个角色在start前的成本汇总
            for (Map.Entry<String, List<KpiProjectTeam>> stringListEntry : roleToProjectTeams.entrySet()) {
                String role = stringListEntry.getKey();
                List<KpiProjectTeam> value = stringListEntry.getValue();
                Float roleCostSum = 0F;
                for (KpiProjectTeam kpiProjectTeam : value) {
                    // 找出这个人这天的工时，再乘以时薪
                    BigDecimal userCost = userIdToCost.get(kpiProjectTeam.getUsrid());
                    // 这个人，并且时间小于7天前
                    List<KpiWorkhour> collect = workhours.stream().filter(s -> s.getUsrid().equals(kpiProjectTeam.getUsrid())
                        && s.getTheDate().compareTo(start) < 0).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(collect)) {
                        int workHours = collect.stream().mapToInt(s -> s.getWorkhour() == null ? 0 : s.getWorkhour().intValue()).sum();
                        roleCostSum += (userCost.floatValue() * workHours);
                    }
                }
                initRoleToCost.put(role, roleCostSum);
            }

            // 图形横坐标
            List<Integer> dayCategories = new ArrayList<>();
            List<String> monthCategories = new ArrayList<>();
            List<KpiProjectDto.ColumnUserData> columnUserDatas = new ArrayList<>();
            if (type == 1) {
                // 按日统计
                Long beginIndex = startDate.toEpochDay() - projectStartDate.toEpochDay() + 1;
                Long between = endDate.toEpochDay() - startDate.toEpochDay() + 1;
                for (int i = 0; i < between.intValue(); i++) {
                    dayCategories.add(beginIndex.intValue() + i);
                }

                for (Map.Entry<String, List<KpiProjectTeam>> stringListEntry : roleToProjectTeams.entrySet()) {
                    String role = stringListEntry.getKey();
                    List<KpiProjectTeam> value = stringListEntry.getValue();

                    KpiProjectDto.ColumnUserData columnUserData = new KpiProjectDto.ColumnUserData();
                    columnUserData.setName(role);
                    // 成本数据
                    List<Float> data = new ArrayList<>();
                    for (Integer dayCategory : dayCategories) {
                        // 取到上一天成本的数据
                        Float previousValue = data.size() > 0 ? data.get(data.size() - 1)
                            : (initRoleToCost.get(role) != null ? initRoleToCost.get(role) : 0F);
                        Float roleDayCost = new Float(previousValue);
                        // d定位到这天
                        LocalDate thisDate = projectStartDate.plusDays(dayCategory - 1);

                        for (KpiProjectTeam kpiProjectTeam : value) {
                            // 找出这个人这天的工时，再乘以时薪
                            BigDecimal userCost = userIdToCost.get(kpiProjectTeam.getUsrid());
                            if (userCost == null) {
                                userCost = new BigDecimal(0);
                            }
                            Optional<KpiWorkhour> first = workhours.stream().filter(s ->
                                s.getUsrid().equals(kpiProjectTeam.getUsrid())
                                    && s.getTheDate().equals(thisDate.toString())).findFirst();
                            if (first.isPresent()) {
                                int hour = first.get().getWorkhour() == null ? 0 : first.get().getWorkhour().intValue();
                                roleDayCost += userCost.floatValue() * hour;
                            }
                        }
                        data.add(roleDayCost);
                    }
                    columnUserData.setData(data);

                    columnUserDatas.add(columnUserData);
                }

            } else if (type == 2) {
                // 按周统计

                LocalDate firstWeek = projectStartDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

                Long beginIndex = startDate.toEpochDay() - firstWeek.toEpochDay();
                Integer startWeekIndex = beginIndex.intValue() / 7;

                // 计算end周和start周相差几周
                Long days = endDate.toEpochDay() - startDate.toEpochDay();
                Integer miuWeek = days.intValue() / 7 + 1;

                for (int i = 0; i < miuWeek; i++) {
                    LocalDate plus = startDate.plusDays(i * 7);
                    dayCategories.add(Integer.valueOf(MyDateUtil.getYearWeek(plus)));
                }

                for (Map.Entry<String, List<KpiProjectTeam>> stringListEntry : roleToProjectTeams.entrySet()) {
                    String role = stringListEntry.getKey();
                    List<KpiProjectTeam> value = stringListEntry.getValue();

                    KpiProjectDto.ColumnUserData columnUserData = new KpiProjectDto.ColumnUserData();
                    columnUserData.setName(role);

                    // 成本数据
                    List<Float> data = new ArrayList<>();
                    for (int i = 0; i < dayCategories.size(); i++) {
                        LocalDate weekMonday = projectStartDate.plusWeeks(startWeekIndex + i);
                        LocalDate weekSunday = weekMonday.plusDays(6);

                        // 取到上一周成本的数据
                        Float previousValue = data.size() > 0 ? data.get(data.size() - 1)
                            : (initRoleToCost.get(role) != null ? initRoleToCost.get(role) : 0F);
                        Float roleWeekCost = new Float(previousValue);

                        for (KpiProjectTeam kpiProjectTeam : value) {
                            // 找出这个人这天的工时，再乘以时薪
                            BigDecimal userCost = userIdToCost.get(kpiProjectTeam.getUsrid());
                            if (userCost == null) {
                                userCost = new BigDecimal(0);
                            }
                            List<KpiWorkhour> collect = workhours.stream().filter(s -> s.getTheDate().compareTo(weekMonday.toString()) >= 0
                                && s.getTheDate().compareTo(weekSunday.toString()) <= 0).collect(Collectors.toList());

                            if (CollectionUtil.isNotEmpty(collect)) {
                                int sum = collect.stream().mapToInt(s -> s.getWorkhour() == null ? 0 : s.getWorkhour().intValue()).sum();
                                roleWeekCost += userCost.floatValue() * sum;
                            }
                        }
                        data.add(roleWeekCost);
                    }
                    columnUserData.setData(data);
                    columnUserDatas.add(columnUserData);
                }

            } else {
                // 按月统计
                LocalDate plusMonths = startDate.plusMonths(0);
                while (plusMonths.compareTo(endDate) <= 0) {
                    String yearMonthInfo = "" + plusMonths.getYear() + "-" +
                        (plusMonths.getMonthValue() >= 10 ? plusMonths.getMonthValue() : "0" + plusMonths.getMonthValue());
                    monthCategories.add(yearMonthInfo);
                    plusMonths = plusMonths.plusMonths(1);
                }

                for (Map.Entry<String, List<KpiProjectTeam>> stringListEntry : roleToProjectTeams.entrySet()) {
                    String role = stringListEntry.getKey();
                    List<KpiProjectTeam> value = stringListEntry.getValue();

                    KpiProjectDto.ColumnUserData columnUserData = new KpiProjectDto.ColumnUserData();
                    columnUserData.setName(role);

                    // 成本数据
                    List<Float> data = new ArrayList<>();
                    for (String monthCategory : monthCategories) {
                        String year = monthCategory.substring(0, 4);
                        String month = monthCategory.substring(5, 7);

                        // 时间范围
                        LocalDate beforeRange = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), 1);
                        LocalDate afterRange = beforeRange.with(TemporalAdjusters.lastDayOfMonth());

                        // 取到上一月成本的数据
                        Float previousValue = data.size() > 0 ? data.get(data.size() - 1)
                            : (initRoleToCost.get(role) != null ? initRoleToCost.get(role) : 0F);
                        Float roleMonthCost = new Float(previousValue);

                        for (KpiProjectTeam kpiProjectTeam : value) {
                            // 找出这个人这天的工时，再乘以时薪
                            BigDecimal userCost = userIdToCost.get(kpiProjectTeam.getUsrid());
                            if (userCost == null) {
                                userCost = new BigDecimal(0);
                            }
                            List<KpiWorkhour> collect = workhours.stream().filter(s -> s.getTheDate().compareTo(beforeRange.toString()) >= 0
                                && s.getTheDate().compareTo(afterRange.toString()) <= 0).collect(Collectors.toList());

                            if (CollectionUtil.isNotEmpty(collect)) {
                                int sum = collect.stream().mapToInt(s -> s.getWorkhour() == null ? 0 : s.getWorkhour().intValue()).sum();
                                roleMonthCost += userCost.floatValue() * sum;
                            }
                        }
                        data.add(roleMonthCost);
                    }
                    columnUserData.setData(data);
                    columnUserDatas.add(columnUserData);
                }
            }

            kpiProjectDto.setDayCategories(dayCategories);
            kpiProjectDto.setMonthCategories(monthCategories);
            kpiProjectDto.setColumnUserDatas(columnUserDatas);

            // 每个柱子成本汇总
            List<Float> columnCostSum = new ArrayList<>();
//                        Float initCostSum = 0F;
//                        if (CollectionUtil.isNotEmpty(initRoleToCost)) {
//                                for (Float value : initRoleToCost.values()) {
//                                        initCostSum += value;
//                                }
//                        }
            Integer xSize = 0;
            if (type == 3) {
                xSize = kpiProjectDto.getMonthCategories().size();
            } else {
                xSize = kpiProjectDto.getDayCategories().size();
            }
            for (int i = 0; i < xSize; i++) {
                Float initCostSum = 0F;
                for (KpiProjectDto.ColumnUserData columnUserData : columnUserDatas) {
                    List<Float> data = columnUserData.getData();
                    initCostSum += data.get(i);
                }
                columnCostSum.add(new Float(initCostSum));
            }
            kpiProjectDto.setColumnCostSum(columnCostSum);


        }


        return kpiProjectDto;
    }

    @Override
    public List<KpiProject> getProjectByUserAndTime(String userId, String time) {
        // 信息校验
        // time格式为2020-10
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(time) || time.length() != 7) {
            return null;
        }

        String year = time.substring(0, 4);
        String month = time.substring(5, 7);
        LocalDate startDay = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), 1);
        LocalDate endDay = startDay.with(TemporalAdjusters.lastDayOfMonth());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        List<KpiProjectTeam> projectTeamList = kpiProjectTeamService.getByUserId(userId);
        if (CollectionUtil.isNotEmpty(projectTeamList)) {
            Set<String> projects = projectTeamList.stream().map(s -> s.getProject()).collect(Collectors.toSet());
            QueryWrapper<KpiProject> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("project", projects);
            List<KpiProject> projectList = this.list(queryWrapper);

            List<KpiProject> timeFilterProject = projectList.stream().filter(s -> {
                return sdf.format(s.getBegda()).compareTo(endDay.toString()) <= 0
                        && sdf.format(s.getEndda()).compareTo(startDay.toString()) >= 0;
            }).collect(Collectors.toList());

            return timeFilterProject;

        }
        return null;
    }


    @Override
    public List<ProjectProcessDto> getProjectProcessInfo () {
        // 根据当前用户获取有哪些项目权限
        Result<SysUser> subject = kpiUserFegin.getSubject();
        SysUser currentUser = subject.getData();

        List<ProjectProcessDto> list = new ArrayList<>();

        List<KpiProject> kpiProjects = this.list();

        for (KpiProject kpiProject : kpiProjects) {
            String project = kpiProject.getProject();
            List<KpiProjectMilestone> projectMilestones = kpiProjectMilestoneService.getByProject(project);

            ProjectProcessDto projectProcessDto = new ProjectProcessDto();
            projectProcessDto.setProject(project);
            projectProcessDto.setId(kpiProject.getId());
            if (CollectionUtil.isNotEmpty(projectMilestones)) {
                // 根据the_date排序，也就是计划时间排序
                projectMilestones.sort(Comparator.comparing(KpiProjectMilestone::getTheDate, Comparator.nullsLast(String::compareTo)));
                // 实际进度
//                                List<Map<String, Object>> actualProcess = new ArrayList<>();
//                                // 计划进度
//                                List<Map<String, Object>> planProcess = new ArrayList<>();

                KpiProjectMilestone first = projectMilestones.get(0);
                // 获取最后更新时间，最后更新人
                Date updateTime = first.getUpdateTime();
                String updateBy = first.getUpdateBy();

                List<ProjectProcessDto.Point> children = new ArrayList<>();
                for (KpiProjectMilestone projectMilestone : projectMilestones) {
                    if ((projectMilestone.getUpdateTime() != null && projectMilestone.getUpdateTime().compareTo(updateTime) > 0) || updateTime == null) {
                        // 对比更新时间，取大的更新时间
                        updateTime = projectMilestone.getUpdateTime();
                        updateBy = projectMilestone.getUpdateBy();
                    }

                    if (projectMilestone.getMilestone() == null) {
                        continue;
                    }

                    ProjectProcessDto.Point point = new ProjectProcessDto.Point();
                    point.setStageShowName(projectMilestone.getMilestone());
                    if (projectMilestone.getEndDate() != null) {
                        point.setActuTime(LocalDateTimeUtil.of(projectMilestone.getEndDate()).toLocalDate().toString());
                    }
                    if (projectMilestone.getTheDate() != null) {
                        point.setPlanTime(projectMilestone.getTheDate());
                    }

                    children.add(point);

                }

                projectProcessDto.setChildren(children);

                if (updateTime != null) {
                    projectProcessDto.setUpdateTime(LocalDateTimeUtil.of(updateTime).toLocalDate().toString());
                }
                if (updateBy != null) {
                    KpiUser kpiUser = kpiUserService.getByUserid(updateBy);
                    if (kpiUser != null) {
                        projectProcessDto.setUpdateUserName(kpiUser.getSname());
                    }
                }
            }
            list.add(projectProcessDto);
        }

        return list;
    }




    @Override
    public List<Integer> getProcessAndEndNum() {
        List<Integer> list = new ArrayList<>();
        // status表示取出进行中的项目，1表示取出已完成的项目
        List<KpiProject> allProject = CollUtil.defaultIfEmpty(this.list(), new ArrayList<>());
        // 排除掉 project_type = 0 的项目
        allProject = allProject.stream().filter(k -> !Objects.equals(k.getProjectType(), 0)).collect(Collectors.toList());
        List<KpiProject> collect = allProject.stream().filter(s -> Objects.equals(s.getProjectStatus(), 1)).collect(Collectors.toList());

        list.add(collect.size());
        list.add(allProject.size() - collect.size());
        return list;
    }

}
