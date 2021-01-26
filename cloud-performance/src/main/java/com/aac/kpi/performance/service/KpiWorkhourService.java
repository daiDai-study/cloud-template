package com.aac.kpi.performance.service;

import com.aac.kpi.performance.entity.KpiWorkhour;
import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.aac.kpi.performance.model.dto.MonthlyWorkhourDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.List;

public interface KpiWorkhourService extends IService<KpiWorkhour> {

    /**
     * 批量 添加或修改 工时记录
     * @param username 用户的域账号信息
     * @param kpiWorkhourList 工时记录
     * @return 操作是否成功
     */
    boolean addOrEditBatch(String username, List<KpiWorkhour> kpiWorkhourList);

    /**
     * 获取 指定用户在某个月在某些项目中的 工时记录
     * @param currentUsername 用户
     * @param theMonth 月份
     * @param projectListByMonth 项目列表
     * @return 工时记录
     */
    List<KpiWorkhour> listByUsernameAndMonth(String currentUsername, String theMonth, List<KpiProjectModel> projectListByMonth);


    JsonNode format(List<KpiWorkhour> kpiWorkhourList, String username, String theMonth, List<KpiProjectModel> projectModelList);

    List<KpiWorkhour> listForConfirm(String currentUsername, String theMonth, String project, Integer theWeek);

    JsonNode formatForConfirm(List<KpiWorkhour> list, List<KpiProjectTeamModel> kpiProjectTeamModelList, String theMonth, Integer theWeek);

    List<KpiWorkhour> listByProjects(List<String> projects);

    Boolean confirm(String currentUsername, List<KpiWorkhour> list);

    List<MonthlyWorkhourDTO> listMonthlyWorkhourByProjectAndMonth(String theMonth, String project);

    /**
     * 获取指定用户在某个项目的某个月份的总记录工时
     * @param username 用户
     * @param theMonth 项目
     * @param project 月份
     * @return 总记录工时
     */
    BigDecimal getWorkhourMonthly(String username, String theMonth, String project);

    BigDecimal getWorkhourConfirmedMonthly(String username, String theMonth, String project);
}
