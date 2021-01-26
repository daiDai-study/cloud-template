package com.aac.kpi.performance.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.exception.BizException;
import com.aac.kpi.performance.constant.PerformanceConst;
import com.aac.kpi.performance.entity.KpiCalendar;
import com.aac.kpi.performance.entity.KpiWorkhour;
import com.aac.kpi.performance.mapper.KpiWorkhourMapper;
import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.aac.kpi.performance.model.dto.MonthlyWorkhourDTO;
import com.aac.kpi.performance.service.KpiCalendarService;
import com.aac.kpi.performance.service.KpiWorkhourService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class KpiWorkhourServiceImpl extends ServiceImpl<KpiWorkhourMapper, KpiWorkhour> implements KpiWorkhourService {

    @Resource
    private KpiCalendarService kpiCalendarService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public boolean addOrEditBatch(String username, List<KpiWorkhour> kpiWorkhourList) {
        // 为提高效率，先循环查找，后批量插入或修改
        // 先 根据用户、项目、日期（三者构成唯一信息） 查找，找到后修改工时，否则添加工时（此时需要找日期对应的周次和月份的信息）
        List<KpiWorkhour> target = new ArrayList<>();
        List<KpiWorkhour> existed = new ArrayList<>();
        List<KpiWorkhour> notExisted = new ArrayList<>();

        // 添加 用户信息
        for (KpiWorkhour kpiWorkhour : kpiWorkhourList) {
            kpiWorkhour.setUsrid(username);
        }

        // 区分
        existedOrNotForEdit(kpiWorkhourList, existed, notExisted);

        // 准备
        prepareForAdd(notExisted);

        target.addAll(existed);
        target.addAll(notExisted);

        return this.saveOrUpdateBatch(target);
    }

    /**
     * 为添加工时做前期准备，设置日期对应的周次和月份的信息
     *
     * @param targetForAdd 需要添加工时的记录
     */
    private void prepareForAdd(List<KpiWorkhour> targetForAdd) {
        Set<String> dateList = targetForAdd.stream().map(KpiWorkhour::getTheDate).collect(Collectors.toSet());
        List<KpiCalendar> kpiCalendars = kpiCalendarService.listByDateList(dateList);
        Map<String, Integer> weekMap = kpiCalendars.stream().collect(Collectors.toMap(KpiCalendar::getTheDate, KpiCalendar::getTheWeek));
        Map<String, String> monthMap = kpiCalendars.stream().collect(Collectors.toMap(KpiCalendar::getTheDate, KpiCalendar::getTheMonth));
        for (KpiWorkhour kpiWorkhour : targetForAdd) {
            String theDate = kpiWorkhour.getTheDate();
            kpiWorkhour.setTheWeek(weekMap.get(theDate));
            kpiWorkhour.setTheMonth(monthMap.get(theDate));
            if (kpiWorkhour.getWorkhour() == null) {
                // 默认为 0.0
                kpiWorkhour.setWorkhour(new BigDecimal("0.0"));
            }
        }
    }

    @Override
    public List<KpiWorkhour> listByUsernameAndMonth(String currentUsername, String theMonth, List<KpiProjectModel> projectListByMonth) {
        List<KpiCalendar> kpiCalendars = kpiCalendarService.listByMonth(theMonth);

        List<KpiWorkhour> target = new ArrayList<>();
        List<KpiWorkhour> all = new ArrayList<>();
        List<KpiWorkhour> existed = new ArrayList<>();
        List<KpiWorkhour> notExisted = new ArrayList<>();

        for (KpiProjectModel kpiProjectModel : projectListByMonth) {
            String project = kpiProjectModel.getProject();
            for (KpiCalendar kpiCalendar : kpiCalendars) {
                KpiWorkhour kpiWorkhour = new KpiWorkhour();
                kpiWorkhour.setUsrid(currentUsername);
                kpiWorkhour.setProject(project);
                kpiWorkhour.setTheDate(kpiCalendar.getTheDate());
                all.add(kpiWorkhour);
            }
        }

        existedOrNotForQuery(all, existed, notExisted);

        prepareForAdd(notExisted);
        this.saveBatch(notExisted);

        target.addAll(existed);
        target.addAll(notExisted);

        return target;
    }

    /**
     * 区分
     *
     * @param all        所有
     * @param existed    已存在的
     * @param notExisted 不存在的
     * @param isForEdit  目的：修改/查询
     */
    private void existedOrNot(List<KpiWorkhour> all, List<KpiWorkhour> existed, List<KpiWorkhour> notExisted, boolean isForEdit) {
        for (KpiWorkhour item : all) {
            String project = item.getProject();
            String theDate = item.getTheDate();
            String username = item.getUsrid();
            if (StrUtil.isEmpty(project) || StrUtil.isEmpty(theDate)) {
                throw new BizException("录入工时时，项目和日期不能为空");
            }
            KpiWorkhour model = baseMapper.getByCompositeUnique(username, project, theDate);
            if (model != null) {
                // 已存在
                if (isForEdit) {
                    model.setWorkhour(item.getWorkhour());
                    // 修改后，状态也修改为未确认状态
                    if (!PerformanceConst.CONFIRMED_STATUS_WAIT.equals(model.getConfirmedStatus())) {
                        model.setConfirmedStatus(PerformanceConst.CONFIRMED_STATUS_WAIT);
                    }
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
    private void existedOrNotForEdit(List<KpiWorkhour> all, List<KpiWorkhour> existed, List<KpiWorkhour> notExisted) {
        existedOrNot(all, existed, notExisted, true);
    }

    /**
     * 区分，目的查询
     *
     * @param all        所有
     * @param existed    已存在的
     * @param notExisted 不存在的
     */
    private void existedOrNotForQuery(List<KpiWorkhour> all, List<KpiWorkhour> existed, List<KpiWorkhour> notExisted) {
        existedOrNot(all, existed, notExisted, false);
    }

    @Override
    public JsonNode format(List<KpiWorkhour> kpiWorkhourList, String username, String theMonth, List<KpiProjectModel> projectModelList) {
        if (kpiWorkhourList.stream().anyMatch(kpiWorkhour -> !Objects.equals(kpiWorkhour.getUsrid(), username))) {
            throw new BizException("格式转换错误：工时列表中包括不属于指定用户的工时信息");
        }
        if (kpiWorkhourList.stream().anyMatch(kpiWorkhour -> !Objects.equals(kpiWorkhour.getTheMonth(), theMonth))) {
            throw new BizException("格式转换错误：工时列表中包括不属于指定月份的工时信息");
        }


        ArrayNode root = objectMapper.createArrayNode();

        if (CollUtil.isEmpty(kpiWorkhourList) || CollUtil.isEmpty(projectModelList)) {
            return root;
        }

        Set<String> originProjectSet = projectModelList.stream().map(KpiProjectModel::getProject).collect(Collectors.toSet());
        Set<String> workhourProjectSet = kpiWorkhourList.stream().map(KpiWorkhour::getProject).collect(Collectors.toSet());

        if (!originProjectSet.equals(workhourProjectSet)) {
            throw new BizException("格式转换错误：工时列表中的项目与指定项目不匹配");
        }

        List<KpiCalendar> kpiCalendars = kpiCalendarService.listByMonth(theMonth);
        List<Integer> weeks = kpiCalendars.stream().map(KpiCalendar::getTheWeek).distinct().sorted(Comparator.comparing(a -> a)).collect(Collectors.toCollection(ArrayList::new));
        Map<Integer, List<KpiCalendar>> weekMap = kpiCalendars.stream().collect(Collectors.groupingBy(KpiCalendar::getTheWeek));

        Map<Integer, Map<String, List<KpiWorkhour>>> weekAndProjectMap = kpiWorkhourList.stream().collect(Collectors.groupingBy(KpiWorkhour::getTheWeek, Collectors.groupingBy(KpiWorkhour::getProject)));
        for (Integer week : weeks) {
            ObjectNode weekNode = objectMapper.createObjectNode();
            Map<String, List<KpiWorkhour>> projectMap = weekAndProjectMap.get(week);
            List<KpiCalendar> calendars = weekMap.get(week);
            calendars.sort(Comparator.comparing(KpiCalendar::getTheDate));
            List<String> datesInWeek = calendars.stream().map(KpiCalendar::getTheDate).collect(Collectors.toList());
            ArrayNode weekData = objectMapper.createArrayNode();
            // 按照 projectModelList 的顺序
            for (KpiProjectModel kpiProjectModel : projectModelList) {
                ObjectNode projectNode = objectMapper.createObjectNode();
                String project = kpiProjectModel.getProject();
                List<KpiWorkhour> kpiWorkhours = projectMap.get(project);
                // 需要根据 datesInWeek 中的每天进行对应，如果没有需要补 0
                List<BigDecimal> workhoursInWeek = new ArrayList<>();
                List<Integer> hoursConfirmedStatus = new ArrayList<>();
                for (String date : datesInWeek) {
                    Optional<KpiWorkhour> workhourOptional = kpiWorkhours.stream().filter(k -> date.equals(k.getTheDate())).findAny();
                    if (workhourOptional.isPresent()) {
                        workhoursInWeek.add(workhourOptional.get().getWorkhour());
                        hoursConfirmedStatus.add(workhourOptional.get().getConfirmedStatus());
                    } else {
                        workhoursInWeek.add(new BigDecimal("0.0"));
                        hoursConfirmedStatus.add(0);
                    }
                }
                projectNode.put("project", project);
                projectNode.put("projectName", kpiProjectModel.getProjectName());
                projectNode.put("beginDate", DateUtil.formatDate(kpiProjectModel.getBegda()));
                projectNode.put("endDate", DateUtil.formatDate(kpiProjectModel.getEndda()));
                projectNode.putPOJO("hours", workhoursInWeek);
                projectNode.putPOJO("hoursConfirmedStatus", hoursConfirmedStatus);
                weekData.add(projectNode);
            }
            weekNode.putPOJO("dateInfo", datesInWeek);
            weekNode.set("dataList", weekData);
            root.add(weekNode);
        }

        return root;
    }

    @Override
    public List<KpiWorkhour> listForConfirm(String currentUsername, String theMonth, String project, Integer theWeek) {
        List<KpiWorkhour> list = baseMapper.listByMonthAndProjectAndWeek(theMonth, project, theWeek);
        // TODO: 排除当前用户还是所有项目中的负责人，暂定为排除当前用户
        list = list.stream().filter(k -> !currentUsername.equals(k.getUsrid())).collect(Collectors.toList());
        return list;
    }

    @Override
    public JsonNode formatForConfirm(List<KpiWorkhour> list, List<KpiProjectTeamModel> kpiProjectTeamModelList, String theMonth, Integer theWeek) {

        List<String> dateInfo = kpiCalendarService.listDateByMonthAndWeek(theMonth, theWeek);

        Set<String> existedDateInfo = list.stream().map(KpiWorkhour::getTheDate).collect(Collectors.toSet());

        // 检查
        if (existedDateInfo.stream().anyMatch(s -> !dateInfo.contains(s))) {
            throw new BizException("格式转换错误：工时列表中包括不属于指定月份和周次的工时信息");
        }

        ObjectNode root = objectMapper.createObjectNode();

        // 按用户进行分组
        Map<String, KpiProjectTeamModel> teamModelMapByUsername = kpiProjectTeamModelList.stream().collect(Collectors.toMap(KpiProjectTeamModel::getUsername, k -> k));
        Map<String, List<KpiWorkhour>> mapByUsername = list.stream().collect(Collectors.groupingBy(KpiWorkhour::getUsrid));

        ArrayNode array = objectMapper.createArrayNode();

        for (Map.Entry<String, List<KpiWorkhour>> entry : mapByUsername.entrySet()) {
            String username = entry.getKey();
            KpiProjectTeamModel teamModel = teamModelMapByUsername.get(username);
            List<KpiWorkhour> kpiWorkhours = entry.getValue();

            // 需要根据 dateInfo 中的每天进行对应，如果没有需要补 0
            List<BigDecimal> hours = new ArrayList<>();
            List<Integer> hoursConfirmedStatus = new ArrayList<>();
            for (String date : dateInfo) {
                Optional<KpiWorkhour> workhourOptional = kpiWorkhours.stream().filter(k -> date.equals(k.getTheDate())).findAny();
                if (workhourOptional.isPresent()) {
                    hours.add(workhourOptional.get().getWorkhour());
                    hoursConfirmedStatus.add(workhourOptional.get().getConfirmedStatus());
                } else {
                    hours.add(new BigDecimal("0.0"));
                    hoursConfirmedStatus.add(0);
                }
            }

            ObjectNode node = objectMapper.createObjectNode();
            node.put("username", username);
            node.put("realname", teamModel.getRealname());
            node.putPOJO("hours", hours);
            node.putPOJO("hoursConfirmedStatus", hoursConfirmedStatus);

            array.add(node);
        }

        root.putPOJO("dateInfo", dateInfo);
        root.set("tableList", array);

        return root;
    }

    @Override
    public List<KpiWorkhour> listByProjects(List<String> projects) {
        if (CollUtil.isEmpty(projects)) {
            return new ArrayList<>();
        }
        return baseMapper.listByProjects(projects);
    }

    @Override
    @Transactional
    public Boolean confirm(String currentUsername, List<KpiWorkhour> list) {
        List<KpiWorkhour> listForEdit = new ArrayList<>();

        for (KpiWorkhour item : list) {
            String project = item.getProject();
            String theDate = item.getTheDate();
            String username = item.getUsrid();
            if (StrUtil.isEmpty(project) || StrUtil.isEmpty(theDate) || StrUtil.isEmpty(username)) {
                throw new BizException("工时确认时，项目、日期和用户不能为空");
            }
            KpiWorkhour model = baseMapper.getByCompositeUnique(username, project, theDate);
            if (model != null && !Objects.equals(model.getConfirmedStatus(), item.getConfirmedStatus())) {
                model.setConfirmedStatus(item.getConfirmedStatus());
                listForEdit.add(model);
                // TODO：针对驳回（或拒绝）的工时信息，需要通知到被确认人，暂时先通过日志方式代替
                if (PerformanceConst.CONFIRMED_STATUS_REJECT.equals(item.getConfirmedStatus())) {
                    
                    log.warn("{} 驳回了 {} 在 {} 项目中 {} 这一天的工时信息，请重新填写并提交。", currentUsername, username, project, theDate);
                }
            }
        }

        return updateBatchById(listForEdit);
    }

    @Override
    public List<MonthlyWorkhourDTO> listMonthlyWorkhourByProjectAndMonth(String theMonth, String project) {
        List<MonthlyWorkhourDTO> list = baseMapper.listMonthlyWorkhourByProjectAndMonth(theMonth, project);
        return CollUtil.defaultIfEmpty(list, new ArrayList<>());
    }


    @Override
    public BigDecimal getWorkhourMonthly(String username, String theMonth, String project) {
        BigDecimal workhourMonthly = baseMapper.getWorkhourMonthly(username, theMonth, project);
        if (workhourMonthly == null) {
            return new BigDecimal("0.0");
        }
        return workhourMonthly;
    }

    @Override
    public BigDecimal getWorkhourConfirmedMonthly(String username, String theMonth, String project) {
        BigDecimal workhourMonthly = baseMapper.getWorkhourConfirmedMonthly(username, theMonth, project);
        if (workhourMonthly == null) {
            return new BigDecimal("0.0");
        }
        return workhourMonthly;
    }
}

