package com.aac.kpi.projectmanage.service;

import com.aac.kpi.projectmanage.dto.CreateKpiProjectDto;
import com.aac.kpi.projectmanage.dto.KpiProjectDto;
import com.aac.kpi.projectmanage.dto.ProjectProcessDto;
import com.aac.kpi.projectmanage.entity.KpiProject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 项目信息表
 * @author： xujie
 * @date：   2020-12-19
 * @version： V1.0
 */
public interface IKpiProjectService extends IService<KpiProject> {

    /**
     * 根据状态查询项目
     * @param status：1进行中，2已完成
     * @return
     */
    List<KpiProjectDto> getKpiProjectByStatus(Integer status);

    /**
     * 创建项目
     * @param createKpiProjectDto
     * @return
     */
    String createProject(CreateKpiProjectDto createKpiProjectDto);

    /**
     * 根据ID删除项目
     * @param id
     */
    void deleteProjectById(Long id);

    /**
     * 获取要编辑项目的信息
     * @param id
     * @return
     */
    CreateKpiProjectDto getEditProjectInfo(Long id);

    /**
     * 编辑已存在的项目
     * @param createKpiProjectDto
     * @return
     */
    String saveEditProject(CreateKpiProjectDto createKpiProjectDto);

    /**
     * 根据项目id获取项目详细信息
     * @param id
     * @return
     */
    KpiProjectDto getProjectDetailInfoById(Long id);

    /**
     * 项目成本图形数据
     * @param id
     * @param start
     * @param end
     * @param type  1按日统计，2按周统计，3按月统计
     * @return
     */
    KpiProjectDto getProjectCostData(Long id, String start, String end, Integer type);

    /**
     * 根据当前用户的域账号和月份获取他所在的所有项目信息
     * @param userId
     * @param time
     * @return
     */
    List<KpiProject> getProjectByUserAndTime(String userId, String time);

    /**
     * 项目绩效页面数据
     * @return
     */
    List<ProjectProcessDto> getProjectProcessInfo();

    /**
     * 获取进行中和结束项目的数量
     * @return
     */
    List<Integer> getProcessAndEndNum();
}
