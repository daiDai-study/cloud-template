package com.aac.kpi.projectmanage.service;

import com.aac.kpi.projectmanage.dto.ProjectTeamDto;
import com.aac.kpi.projectmanage.entity.KpiProjectTeam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 项目团队信息表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
public interface IKpiProjectTeamService extends IService<KpiProjectTeam> {

    List<KpiProjectTeam> getByProjectNames(List<String> projectNames);

    List<KpiProjectTeam> getByProject(String project);

    /**
     * 获取项目成员明细
     * @param project
     * @return
     */
    List<ProjectTeamDto> getProjectTeamDtoByProject(String project);

    /**
     * 编辑项目成员信息
     * @param projectTeamDto
     * @return
     */
    String editProjectTeam(ProjectTeamDto projectTeamDto);

    List<KpiProjectTeam> getByUserId(String userId);

    List<KpiProjectTeam> getByUserIdAsOwner(String userId);
}
