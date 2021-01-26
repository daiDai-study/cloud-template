package com.aac.kpi.projectmanage.service;

import com.aac.kpi.projectmanage.entity.KpiProjectMilestone;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 项目里程碑事件表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
public interface IKpiProjectMilestoneService extends IService<KpiProjectMilestone> {

    /**
     * 获取项目里程碑事件
     * @param id
     * @return
     */
    List<KpiProjectMilestone> getProjectEventById(Long id);

    List<KpiProjectMilestone> getByProject(String project);

    /**
     * 创建事件
     * @param kpiProjectMilestone
     * @return
     */
    String createProjectEvent(KpiProjectMilestone kpiProjectMilestone);

    /**
     * 一次性提交项目事件
     * @param kpiProjectMilestones
     * @return
     */
    String submitAllEvent(List<KpiProjectMilestone> kpiProjectMilestones);

    String delById(Long id);
}
