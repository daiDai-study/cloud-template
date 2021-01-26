package com.aac.kpi.performance.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.exception.BizException;
import com.aac.kpi.performance.entity.KpiReviewTeam;
import com.aac.kpi.performance.entity.KpiWorkdesc;
import com.aac.kpi.performance.entity.KpiWorkhour;
import com.aac.kpi.performance.mapper.KpiReviewTeamMapper;
import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.aac.kpi.performance.service.KpiReviewTeamService;
import com.aac.kpi.performance.service.KpiWorkhourService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class KpiReviewTeamServiceImpl extends ServiceImpl<KpiReviewTeamMapper, KpiReviewTeam> implements KpiReviewTeamService {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private KpiWorkhourService kpiWorkhourService;

    /**
     * 区分
     *
     * @param all        所有
     * @param existed    已存在的
     * @param notExisted 不存在的
     */
    private void existedOrNot(List<KpiReviewTeam> all, List<KpiReviewTeam> existed, List<KpiReviewTeam> notExisted, boolean isForEdit) {
        for (KpiReviewTeam item : all) {
            String project = item.getProject();
            String theMonth = item.getTheMonth();
            String assessee = item.getUsrid();
            String assessor = item.getAssessUsrid();
            if (StrUtil.isEmpty(project) || StrUtil.isEmpty(theMonth)) {
                throw new BizException("录入工时时，项目和月份不能为空");
            }
            if (StrUtil.isEmpty(assessee) || StrUtil.isEmpty(assessor)) {
                throw new BizException("录入工时时，评估人和被评估人都不能为空");
            }
            KpiReviewTeam model = baseMapper.getByCompositeUnique(assessee, assessor, project, theMonth);
            if (model != null) {
                // 已存在
                if(isForEdit){
                    model.setKpi1(item.getKpi1());
                    model.setKpi2(item.getKpi2());
                    model.setKpi(item.getKpi());
                }
                existed.add(model);
            } else {
                // 没有，需新增
                notExisted.add(item);
            }
        }
    }

    /**
     * 区分，目的修改
     *
     * @param all        所有
     * @param existed    已存在的
     * @param notExisted 不存在的
     */
    private void existedOrNotForEdit(List<KpiReviewTeam> all, List<KpiReviewTeam> existed, List<KpiReviewTeam> notExisted) {
        existedOrNot(all, existed, notExisted, true);
    }

    /**
     * 区分，目的查询
     *
     * @param all        所有
     * @param existed    已存在的
     * @param notExisted 不存在的
     */
    private void existedOrNotForQuery(List<KpiReviewTeam> all, List<KpiReviewTeam> existed, List<KpiReviewTeam> notExisted) {
        existedOrNot(all, existed, notExisted, false);
    }

    @Override
    public List<KpiReviewTeam> listByUsernameAndMonth(String currentUsername, String theMonth, List<KpiProjectTeamModel> kpiProjectTeamModelList) {
        List<KpiReviewTeam> target = new ArrayList<>();
        List<KpiReviewTeam> all = new ArrayList<>();
        List<KpiReviewTeam> existed = new ArrayList<>();
        List<KpiReviewTeam> notExisted = new ArrayList<>();

        for (KpiProjectTeamModel kpiProjectTeamModel : kpiProjectTeamModelList) {
            String project = kpiProjectTeamModel.getProject();
            String username = kpiProjectTeamModel.getUsername();
            KpiReviewTeam model = new KpiReviewTeam();
            model.setAssessUsrid(currentUsername);
            model.setUsrid(username);
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
    public JsonNode format(List<KpiReviewTeam> list, String currentUsername, String theMonth, List<KpiProjectTeamModel> kpiProjectTeamModelList) {
        if (list.stream().anyMatch(item -> !Objects.equals(item.getAssessUsrid(), currentUsername))) {
            throw new BizException("格式转换错误：成员互评列表中包括不属于指定用户（作为评估人）的信息");
        }
        if (list.stream().anyMatch(item -> !Objects.equals(item.getTheMonth(), theMonth))) {
            throw new BizException("格式转换错误：成员互评列表中包括不属于指定月份的信息");
        }

        // 根据被评估人进行map
        Map<String, KpiProjectTeamModel> mapByUsername = kpiProjectTeamModelList.stream().collect(Collectors.toMap(KpiProjectTeamModel::getUsername, k -> k));

        Set<String> projects = kpiProjectTeamModelList.stream().map(KpiProjectTeamModel::getProject).collect(Collectors.toSet());
        if(CollUtil.isEmpty(projects) || projects.size() > 1){
            throw new BizException("格式转换错误：成员互评列表中没有项目信息或者有多个项目信息");
        }
        String project = projects.toArray(new String[0])[0];

        ArrayNode root = objectMapper.createArrayNode();
        for (KpiReviewTeam item : list) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            KpiProjectTeamModel model = mapByUsername.get(item.getUsrid());
            objectNode.put("username", model.getUsername());
            objectNode.put("realname", model.getRealname());
            objectNode.put("rolename", model.getRoleName());
            objectNode.put("workhourMonthly", kpiWorkhourService.getWorkhourConfirmedMonthly(model.getUsername(), theMonth, project));
            objectNode.put("kpi1", item.getKpi1());
            objectNode.put("kpi2", item.getKpi2());
            objectNode.put("kpi", item.getKpi());
            root.add(objectNode);
        }

        return root;
    }

    @Override
    public Boolean addOrEditBatch(String currentUsername, List<KpiReviewTeam> kpiReviewTeamList) {
        // 为提高效率，先循环查找，后批量插入或修改
        // 先 根据用户、项目、日期（三者构成唯一信息） 查找，找到后修改工时，否则添加工时（此时需要找日期对应的周次和月份的信息）
        List<KpiReviewTeam> target = new ArrayList<>();
        List<KpiReviewTeam> existed = new ArrayList<>();
        List<KpiReviewTeam> notExisted = new ArrayList<>();

        // 添加 用户信息（评估人）
        for (KpiReviewTeam item : kpiReviewTeamList) {
            item.setAssessUsrid(currentUsername);
        }

        // 区分
        existedOrNotForEdit(kpiReviewTeamList, existed, notExisted);

        target.addAll(existed);
        target.addAll(notExisted);

        return this.saveOrUpdateBatch(target);
    }

    @Override
    public List<KpiReviewTeam> listByProjectAndMonth(String project, String theMonth) {
        List<KpiReviewTeam> list = baseMapper.listByProjectAndMonth(project, theMonth);
        return CollUtil.defaultIfEmpty(list, new ArrayList<>());
    }
}
