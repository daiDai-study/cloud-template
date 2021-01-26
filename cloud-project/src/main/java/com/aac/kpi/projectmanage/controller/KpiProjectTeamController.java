package com.aac.kpi.projectmanage.controller;

import com.aac.kpi.projectmanage.dto.ProjectTeamDto;
import com.aac.kpi.projectmanage.service.IKpiProjectTeamService;
import com.aac.kpi.projectmanage.util.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: Controller
 * @Description: 项目团队信息表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@RestController
@RequestMapping("/kpiProjectTeam")
@Slf4j
@Api("项目团队信息表")
public class KpiProjectTeamController {
	@Autowired
	private IKpiProjectTeamService kpiProjectTeamService;

	@GetMapping("/getProjectTeamByProjectDto")
	public Result<List<ProjectTeamDto>> getProjectTeamDtoByProject(@RequestParam("project")String project) {
		Result<List<ProjectTeamDto>> result = new Result<>();
		List<ProjectTeamDto> list = kpiProjectTeamService.getProjectTeamDtoByProject(project);
		result.setResult(list);
		result.success("获取信息成功");
		return result;
	}

	@PostMapping("/editProjectTeam")
	public Result<String> editProjectTeam(@RequestBody ProjectTeamDto projectTeamDto) {
		Result<String> result = new Result<>();
		String ss  = kpiProjectTeamService.editProjectTeam(projectTeamDto);
		result.setResult(ss);
		result.success("编辑信息成功");
		return result;
	}


}
