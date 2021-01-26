package com.aac.kpi.projectmanage.service.impl;

import com.aac.kpi.projectmanage.dto.SysUser;
import com.aac.kpi.projectmanage.entity.KpiProject;
import com.aac.kpi.projectmanage.entity.KpiProjectMilestone;
import com.aac.kpi.projectmanage.fegin.KpiUserFegin;
import com.aac.kpi.projectmanage.mapper.KpiProjectMilestoneMapper;
import com.aac.kpi.projectmanage.service.IKpiProjectMilestoneService;
import com.aac.kpi.projectmanage.service.IKpiProjectService;
import com.aac.kpi.projectmanage.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @Description: 项目里程碑事件表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@Service
public class KpiProjectMilestoneServiceImpl extends ServiceImpl<KpiProjectMilestoneMapper, KpiProjectMilestone> implements IKpiProjectMilestoneService {
    @Resource
    private KpiProjectMilestoneMapper kpiProjectMilestoneMapper;
    @Autowired
    private IKpiProjectService kpiProjectService;
    @Autowired
    private KpiUserFegin kpiUserFegin;

    @Override
    public List<KpiProjectMilestone> getProjectEventById(Long id) {
        KpiProject kpiProject = kpiProjectService.getById(id);
        List<KpiProjectMilestone> milestoneList = this.getByProject(kpiProject.getProject());
        // 按照时间排序
        milestoneList.sort(Comparator.comparing(KpiProjectMilestone::getTheDate, Comparator.nullsLast(String::compareTo)));
        return milestoneList;
    }

    @Override
    public List<KpiProjectMilestone> getByProject(String project) {
        QueryWrapper<KpiProjectMilestone> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project", project);
        return this.list(queryWrapper);
    }

    @Override
    public String createProjectEvent(KpiProjectMilestone kpiProjectMilestone) {
        // 获取当前登录用户
        Result<SysUser> subject = kpiUserFegin.getSubject();
        SysUser currentUser = subject.getData();
        kpiProjectMilestone.setCreateBy(currentUser.getUsername());
        kpiProjectMilestone.setUpdateBy(currentUser.getUsername());
        this.save(kpiProjectMilestone);

        return "1";
    }

    @Override
    public String submitAllEvent(List<KpiProjectMilestone> kpiProjectMilestones) {
        // 获取当前登录用户
        Result<SysUser> subject = kpiUserFegin.getSubject();
        SysUser currentUser = subject.getData();

        // 如果有id说明是更新，如果没有，新增
        for (KpiProjectMilestone kpiProjectMilestone : kpiProjectMilestones) {
            if (kpiProjectMilestone.getId() != null) {
                // update
                KpiProjectMilestone updateBean = this.getById(kpiProjectMilestone.getId());
                updateBean.setMilestoneEvent(kpiProjectMilestone.getMilestoneEvent());
                updateBean.setTheDate(kpiProjectMilestone.getTheDate());
                updateBean.setRemark(kpiProjectMilestone.getRemark());
                updateBean.setMilestone(kpiProjectMilestone.getMilestone());
                updateBean.setUpdateTime(new Date());
                updateBean.setUpdateBy(currentUser.getUsername());
                updateBean.setEndDate(kpiProjectMilestone.getEndDate());

                this.updateById(updateBean);

            } else {
                // insert
                kpiProjectMilestone.setCreateBy(currentUser.getUsername());
                kpiProjectMilestone.setUpdateBy(currentUser.getUsername());
                this.save(kpiProjectMilestone);
            }
        }
        return "1";
    }

    @Override
    public String delById(Long id) {
        this.removeById(id);
        return "1";
    }
}
