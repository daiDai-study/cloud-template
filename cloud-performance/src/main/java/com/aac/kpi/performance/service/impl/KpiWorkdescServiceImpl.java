package com.aac.kpi.performance.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.constant.CommonConst;
import com.aac.kpi.common.exception.BizException;
import com.aac.kpi.performance.entity.KpiReviewTeam;
import com.aac.kpi.performance.entity.KpiWorkdesc;
import com.aac.kpi.performance.entity.KpiWorkhour;
import com.aac.kpi.performance.mapper.KpiWorkdescMapper;
import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.aac.kpi.performance.model.dto.MonthlyWorkhourDTO;
import com.aac.kpi.performance.model.vo.KpiReviewTeamVO;
import com.aac.kpi.performance.model.vo.KpiTeamMemberMonthlyInfoVO;
import com.aac.kpi.performance.service.KpiReviewTeamService;
import com.aac.kpi.performance.service.KpiScoreConfService;
import com.aac.kpi.performance.service.KpiWorkdescService;
import com.aac.kpi.performance.service.KpiWorkhourService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class KpiWorkdescServiceImpl extends ServiceImpl<KpiWorkdescMapper, KpiWorkdesc> implements KpiWorkdescService {

    @Resource
    private KpiWorkhourService kpiWorkhourService;

    @Resource
    private KpiReviewTeamService kpiReviewTeamService;

    @Resource
    private KpiScoreConfService kpiScoreConfService;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 区分，目的修改
     *
     * @param all        所有
     * @param existed    已存在的
     * @param notExisted 不存在的
     */
    private void existedOrNotForEdit(List<KpiWorkdesc> all, List<KpiWorkdesc> existed, List<KpiWorkdesc> notExisted) {
        existedOrNot(all, existed, notExisted, true);
    }

    /**
     * 区分，目的查询
     *
     * @param all        所有
     * @param existed    已存在的
     * @param notExisted 不存在的
     */
    private void existedOrNotForQuery(List<KpiWorkdesc> all, List<KpiWorkdesc> existed, List<KpiWorkdesc> notExisted) {
        existedOrNot(all, existed, notExisted, false);
    }

    /**
     * 区分
     *
     * @param all        所有
     * @param existed    已存在的
     * @param notExisted 不存在的
     * @param isForEdit  目的：修改/查询
     */
    private void existedOrNot(List<KpiWorkdesc> all, List<KpiWorkdesc> existed, List<KpiWorkdesc> notExisted, boolean isForEdit) {
        for (KpiWorkdesc item : all) {
            String project = item.getProject();
            String theMonth = item.getTheMonth();
            String username = item.getUsrid();
            if (StrUtil.isEmpty(project) || StrUtil.isEmpty(theMonth)) {
                throw new BizException("录入工时时，项目和月份不能为空");
            }
            KpiWorkdesc model = baseMapper.getByCompositeUnique(username, project, theMonth);
            if (model != null) {
                // 已存在
                if (isForEdit) {
                    model.setWorkdesc(item.getWorkdesc());
                }
                existed.add(model);
            } else {
                // 没有，需新增
                notExisted.add(item);
            }
        }
    }

    @Override
    public List<KpiWorkdesc> listByUsernameAndMonth(String currentUsername, String theMonth, List<KpiProjectModel> projectModelList) {
        List<KpiWorkdesc> target = new ArrayList<>();
        List<KpiWorkdesc> all = new ArrayList<>();
        List<KpiWorkdesc> existed = new ArrayList<>();
        List<KpiWorkdesc> notExisted = new ArrayList<>();

        for (KpiProjectModel kpiProjectModel : projectModelList) {
            String project = kpiProjectModel.getProject();
            KpiWorkdesc model = new KpiWorkdesc();
            model.setUsrid(currentUsername);
            model.setProject(project);
            model.setTheMonth(theMonth);
            all.add(model);
        }

        existedOrNotForQuery(all, existed, notExisted);

        this.saveBatch(notExisted);

        target.addAll(existed);
        target.addAll(notExisted);

        return target;
    }

    @Override
    public JsonNode format(List<KpiWorkdesc> kpiWorkdescList, String currentUsername, String theMonth, List<KpiProjectModel> projectModelList) {
        if (kpiWorkdescList.stream().anyMatch(kpiWorkhour -> !Objects.equals(kpiWorkhour.getUsrid(), currentUsername))) {
            throw new BizException("格式转换错误：月度绩效描述列表中包括不属于指定用户的信息");
        }
        if (kpiWorkdescList.stream().anyMatch(kpiWorkhour -> !Objects.equals(kpiWorkhour.getTheMonth(), theMonth))) {
            throw new BizException("格式转换错误：月度绩效描述列表中包括不属于指定月份的信息");
        }

        Set<String> originProjectSet = projectModelList.stream().map(KpiProjectModel::getProject).collect(Collectors.toSet());
        Set<String> workhourProjectSet = kpiWorkdescList.stream().map(KpiWorkdesc::getProject).collect(Collectors.toSet());

        if (!originProjectSet.equals(workhourProjectSet)) {
            throw new BizException("格式转换错误：月度绩效描述列表中的项目与指定项目不匹配");
        }

        Map<String, String> projectNameMap = projectModelList.stream().collect(Collectors.toMap(KpiProjectModel::getProject, KpiProjectModel::getProjectName));

        ArrayNode root = objectMapper.createArrayNode();
        for (KpiWorkdesc kpiWorkdesc : kpiWorkdescList) {
            String project = kpiWorkdesc.getProject();
            BigDecimal workhourMonthly = kpiWorkhourService.getWorkhourMonthly(currentUsername, theMonth, project);
            // 月度绩效描述显示的项目月度工时必须大于 0
            if (project != null && workhourMonthly.compareTo(new BigDecimal("0")) > 0) {
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("id", kpiWorkdesc.getId().toString());
                objectNode.put("project", project);
                objectNode.put("projectName", projectNameMap.get(project));
                objectNode.put("workdesc", kpiWorkdesc.getWorkdesc());
                objectNode.put("workhourMonthly", workhourMonthly);
                root.add(objectNode);
            }
        }

        return root;
    }

    @Override
    @Transactional
    public boolean addOrEditBatch(String currentUsername, List<KpiWorkdesc> kpiWorkdescList) {
        // 为提高效率，先循环查找，后批量插入或修改
        // 先 根据用户、项目、日期（三者构成唯一信息） 查找，找到后修改工时，否则添加工时（此时需要找日期对应的周次和月份的信息）
        List<KpiWorkdesc> target = new ArrayList<>();
        List<KpiWorkdesc> existed = new ArrayList<>();
        List<KpiWorkdesc> notExisted = new ArrayList<>();

        // 添加 用户信息
        for (KpiWorkdesc item : kpiWorkdescList) {
            item.setUsrid(currentUsername);
        }

        // 区分
        existedOrNotForEdit(kpiWorkdescList, existed, notExisted);

        target.addAll(existed);
        target.addAll(notExisted);

        return this.saveOrUpdateBatch(target);
    }

    @Override
    public List<KpiTeamMemberMonthlyInfoVO> listForScoring(String currentUsername, String theMonth, String project, List<KpiProjectTeamModel> kpiProjectTeamModelList) {

        // 项目检查
        Set<String> projectSet = kpiProjectTeamModelList.stream().map(KpiProjectTeamModel::getProject).collect(Collectors.toSet());
        if (projectSet.size() != 1 || !projectSet.contains(project)) {
            throw new BizException("项目（" + project + "）中包括不属于指定该项目的成员");
        }

        // 项目成员的基本信息，需要在排除用户之前进行，否则出现成员互评中没有对应用户信息的现象
        Map<String, KpiProjectTeamModel> teamModelMapByUsername = kpiProjectTeamModelList.stream().collect(Collectors.toMap(KpiProjectTeamModel::getUsername, k -> k));

        // TODO:应该排除当前用户？还是排除项目负责人？还是怎样？暂定排除当前用户
        kpiProjectTeamModelList = kpiProjectTeamModelList.stream().filter(k -> !currentUsername.equals(k.getUsername())).collect(Collectors.toList());

        // 获取所有的成员 -- 可用于校验项目成员是否全部都填写月度绩效描述
        Set<String> usernameList = kpiProjectTeamModelList.stream().map(KpiProjectTeamModel::getUsername).collect(Collectors.toSet());

        // 项目成员对应的月度绩效描述、po的考核分数和等级
        List<KpiWorkdesc> kpiWorkdescList = baseMapper.listByProjectAndMonth(project, theMonth);
        Map<String, KpiWorkdesc> workdescMapByUsername = kpiWorkdescList.stream().collect(Collectors.toMap(KpiWorkdesc::getUsrid, k -> k));

        // 项目成员在该项目中的月度工时
        List<MonthlyWorkhourDTO> monthlyWorkhourDTOList = kpiWorkhourService.listMonthlyWorkhourByProjectAndMonth(theMonth, project);
        Map<String, MonthlyWorkhourDTO> monthlyWorkhourMapByUsername = monthlyWorkhourDTOList.stream().collect(Collectors.toMap(MonthlyWorkhourDTO::getUsername, k -> k));

        // 项目成员在项目（日常工作）中的月度工时
        List<MonthlyWorkhourDTO> monthlyWorkhourDTOListForDaiWork = kpiWorkhourService.listMonthlyWorkhourByProjectAndMonth(theMonth, CommonConst.PROJECT_DAILY_WORK);
        Map<String, MonthlyWorkhourDTO> monthlyWorkhourForDaiWorkMapByUsername = monthlyWorkhourDTOListForDaiWork.stream().collect(Collectors.toMap(MonthlyWorkhourDTO::getUsername, k -> k));

        // 项目成员的互评信息（作为被评价人）
        List<KpiReviewTeam> kpiReviewTeamList = kpiReviewTeamService.listByProjectAndMonth(project, theMonth);
        Map<String, List<KpiReviewTeam>> kpiReviewTeamListMapByAccessee = kpiReviewTeamList.stream().collect(Collectors.groupingBy(KpiReviewTeam::getUsrid));

        List<KpiTeamMemberMonthlyInfoVO> list = new ArrayList<>();
        for (Map.Entry<String, KpiProjectTeamModel> entry : teamModelMapByUsername.entrySet()) {
            String username = entry.getKey();
            KpiProjectTeamModel projectTeamModel = entry.getValue();
            KpiWorkdesc kpiWorkdesc = workdescMapByUsername.get(username);
            MonthlyWorkhourDTO monthlyWorkhour = monthlyWorkhourMapByUsername.get(username);
            MonthlyWorkhourDTO monthlyWorkhourForDaiWork = monthlyWorkhourForDaiWorkMapByUsername.get(username);
            List<KpiReviewTeam> teamList = CollUtil.defaultIfEmpty(kpiReviewTeamListMapByAccessee.get(username), new ArrayList<>());

            KpiTeamMemberMonthlyInfoVO item = new KpiTeamMemberMonthlyInfoVO();

            // 基本信息
            item.setUsername(username);
            if (projectTeamModel != null) {
                item.setRealname(projectTeamModel.getRealname());
                item.setRole(projectTeamModel.getRole());
                item.setRoleName(projectTeamModel.getRoleName());
            }

            // 月度绩效描述、po的考核分数和等级
            if (kpiWorkdesc != null) {
                item.setWorkdesc(kpiWorkdesc.getWorkdesc());
                item.setPoScore(kpiWorkdesc.getPoScore());
                item.setPoRank(kpiWorkdesc.getPoRank());
            } else {
                item.setWorkdesc(null);
                item.setPoScore(null);
                item.setPoRank(null);
            }


            // 月度工时
            if (monthlyWorkhour != null) {
                item.setWorkhourForProject(monthlyWorkhour.getMonthlyWorkhourConfirmed());
            } else {
                item.setWorkhourForProject(0.0f);
            }

            if (monthlyWorkhourForDaiWork != null) {
                item.setWorkhourForDailyWork(monthlyWorkhourForDaiWork.getMonthlyWorkhourConfirmed());
            } else {
                item.setWorkhourForDailyWork(0.0f);
            }

            // 互评信息
            List<KpiReviewTeamVO> kpiReviewTeamVOList = teamList.stream().map(kpiReviewTeam -> {
                String assessor = kpiReviewTeam.getAssessUsrid();
                String assessee = kpiReviewTeam.getUsrid();
                KpiProjectTeamModel teamModelForAssessor = teamModelMapByUsername.get(assessor);
                KpiProjectTeamModel teamModelForAssessee = teamModelMapByUsername.get(assessee);
                KpiReviewTeamVO kpiReviewTeamVO = new KpiReviewTeamVO();
                kpiReviewTeamVO.setProject(project);
                kpiReviewTeamVO.setTheMonth(theMonth);
                kpiReviewTeamVO.setAssessee(assessee);
                kpiReviewTeamVO.setAssessor(assessor);
                if (teamModelForAssessee != null) {
                    kpiReviewTeamVO.setAssesseeName(teamModelForAssessee.getRealname());
                }
                if (teamModelForAssessor != null) {
                    kpiReviewTeamVO.setAsserssorName(teamModelForAssessor.getRealname());
                }
                kpiReviewTeamVO.setKpi1(kpiReviewTeam.getKpi1());
                kpiReviewTeamVO.setKpi2(kpiReviewTeam.getKpi2());
                kpiReviewTeamVO.setKpi(kpiReviewTeam.getKpi());
                return kpiReviewTeamVO;
            }).collect(Collectors.toList());
            item.setKpiReviewTeamVOList(kpiReviewTeamVOList);

            // 互评综合分数平均
            double average = kpiReviewTeamVOList.stream().mapToInt(k -> {
                Integer kpi = k.getKpi();
                return kpi != null ? kpi : 0;
            }).average().orElse(0);
            item.setAverageKpi((int) average);

            list.add(item);
        }

        return list;
    }

    @Override
    public boolean confirmScore(String currentUsername, List<KpiWorkdesc> list) {
        List<KpiWorkdesc> targetForEdit = new ArrayList<>();
        for (KpiWorkdesc item : list) {
            String project = item.getProject();
            String theMonth = item.getTheMonth();
            String username = item.getUsrid();
            if (StrUtil.isEmpty(project) || StrUtil.isEmpty(theMonth) || StrUtil.isEmpty(username)) {
                throw new BizException("确认考核分数和等级时，项目、月份和被考核人不能为空");
            }
            KpiWorkdesc model = baseMapper.getByCompositeUnique(username, project, theMonth);
            Integer poScore = item.getPoScore();
            if (model != null && poScore != null) {
                model.setPoScore(poScore);
                String poRank = item.getPoRank();
                if (StrUtil.isEmpty(poRank)) {
                    poRank = kpiScoreConfService.getRankByScore(poScore);
                }
                model.setPoRank(poRank);
                targetForEdit.add(model);
            }
        }
        return this.updateBatchById(targetForEdit);
    }
}
