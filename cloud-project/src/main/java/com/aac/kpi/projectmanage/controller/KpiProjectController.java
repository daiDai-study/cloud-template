package com.aac.kpi.projectmanage.controller;

import com.aac.kpi.projectmanage.dto.CreateKpiProjectDto;
import com.aac.kpi.projectmanage.dto.KpiProjectDto;
import com.aac.kpi.projectmanage.dto.ProjectProcessDto;
import com.aac.kpi.projectmanage.entity.KpiProject;
import com.aac.kpi.projectmanage.service.IKpiProjectService;
import com.aac.kpi.projectmanage.service.KpiProjectOutService;
import com.aac.kpi.projectmanage.util.RequestHeaderInfo;
import com.aac.kpi.projectmanage.util.Result;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: Controller
 * @Description: 项目信息表
 * @author： xujie
 * @date：   2020-12-19
 * @version： V1.0
 */
@RestController
@RequestMapping("/kpiProject")
@Slf4j
@Api("项目信息表")
public class KpiProjectController {
	@Autowired
	private IKpiProjectService kpiProjectService;
	@Autowired
	private KpiProjectOutService kpiProjectOutService;


	@GetMapping("/getAllKpiProject")
	public Result<List<KpiProject>> getAllKpiProject() {
		Result<List<KpiProject>> result = new Result<>();
		List<KpiProject> list = kpiProjectService.list();
		result.setResult(list);
		result.success("获取信息成功");
		System.out.println(result);
		return result;
	}

	/**
	 * 获取进行中或者已完成的项目
	 * @param status
	 * @return
	 */
	@GetMapping("/getKpiProjectByStatus")
	public Result<List<KpiProjectDto>> getKpiProjectByStatus(@RequestParam("status")Integer status) {
		Result<List<KpiProjectDto>> result = new Result<>();
		List<KpiProjectDto> list = kpiProjectService.getKpiProjectByStatus(status);
		result.setResult(list);
		result.success("获取信息成功");
		System.out.println(RequestHeaderInfo.token);
		return result;
	}

	/**
	 * 创建项目
	 * @param createKpiProjectDto
	 * @return
	 */
	@PostMapping("/createProject")
	public Result<String> createProject(@RequestBody CreateKpiProjectDto createKpiProjectDto) {
		Result<String> result = new Result<>();
		String ss = kpiProjectService.createProject(createKpiProjectDto);
		result.setResult(ss);
		result.success("创建项目成功");
		return result;
	}

	/**
	 * 删除项目
	 * @param id
	 * @return
	 */
	@GetMapping("/deleteProjectById")
	public Result<Integer> deleteProjectById(@RequestParam("id")Long id) {
		Result<Integer> result = new Result<>();
		kpiProjectService.deleteProjectById(id);
		return result;
	}

	/**
	 * 编辑项目前，获取项目信息
	 * @param id
	 * @return
	 */
	@GetMapping("/getEditProjectInfo")
	public Result<CreateKpiProjectDto> getEditProjectInfo(@RequestParam("id")Long id) {
		Result<CreateKpiProjectDto> result = new Result<>();
		CreateKpiProjectDto createKpiProjectDto = kpiProjectService.getEditProjectInfo(id);
		result.setResult(createKpiProjectDto);
		result.success("获取信息成功");
		return result;
	}

	/**
	 * 保存编辑的项目信息
	 * @param createKpiProjectDto
	 * @return
	 */
	@PostMapping("/saveEditProject")
	public Result<String> saveEditProject(@RequestBody CreateKpiProjectDto createKpiProjectDto) {
		Result<String> result = new Result<>();
		String ss = kpiProjectService.saveEditProject(createKpiProjectDto);
		result.setResult(ss);
		result.success("更新项目成功");
		return result;
	}

	/**
	 * 获取项目概况上半部分数据
	 * @param id
	 * @return
	 */
	@GetMapping("/getProjectDetailInfoById")
	public Result<KpiProjectDto> getProjectDetailInfoById(@RequestParam("id")Long id) {
		Result<KpiProjectDto> result = new Result<>();
		KpiProjectDto kpiProjectDto = kpiProjectService.getProjectDetailInfoById(id);
		result.setResult(kpiProjectDto);
		result.success("获取信息成功");
		return result;
	}

	/**
	 * 获取项目概况图形数据
	 * @param id
	 * @return
	 */
	@GetMapping("/getProjectCostData")
	public Result<KpiProjectDto> getProjectCostData(@RequestParam("id")Long id, @RequestParam("start")String start,
													@RequestParam("end")String end, @RequestParam("type")Integer type) {
		Result<KpiProjectDto> result = new Result<>();
		KpiProjectDto kpiProjectDto = kpiProjectService.getProjectCostData(id, start, end, type);
		result.setResult(kpiProjectDto);
		result.success("获取信息成功");
		return result;
	}

    /**
     * 根据当前用户的域账号和月份获取他所在的所有项目信息
     * @param userId
     * @param time
     * @return
     */
    @GetMapping("/getProjectByUserAndTime")
	public List<KpiProject> getProjectByUserAndTime(@RequestParam("userId")String userId, @RequestParam("time")String time) {
        List<KpiProject> list = kpiProjectService.getProjectByUserAndTime(userId, time);
        return list;
    }

    /**
     * 项目绩效页面数据
     * @return
     */
    @GetMapping("/getProjectProcessInfo")
    public Result<List<ProjectProcessDto>> getProjectProcessInfo() {
        Result<List<ProjectProcessDto>> result = new Result<>();
        List<ProjectProcessDto> list = kpiProjectService.getProjectProcessInfo();
        result.setResult(list);
        result.success("获取信息成功");
        return result;
    }

	/**
	 * 获取进行中和结束项目的数量
	 * @return
	 */
	@GetMapping("/getProcessAndEndNum")
	public Result<List<Integer>> getProcessAndEndNum() {
		Result<List<Integer>> result = new Result<>();
		List<Integer>  list =kpiProjectService.getProcessAndEndNum();
		result.setResult(list);
		result.success("获取信息成功");
		return result;
	}












	//--------给予外部微服务的接口-------------------//
	@GetMapping("/getProjectListWithoutRequiredByUsername")
	List<JSONObject> getProjectListWithoutRequiredByUsername(@RequestParam("username") String username){
		List<JSONObject> list = kpiProjectOutService.getProjectListWithoutRequiredByUsername(username);
		return list;
	}

	@GetMapping("/getProjectListWithRequiredByUsername")
	List<JSONObject> getProjectListWithRequiredByUsername(@RequestParam("username") String username){
		List<JSONObject> list = kpiProjectOutService.getProjectListWithRequiredByUsername(username);
		return list;
	}

	@GetMapping("/getProjectListByUsernameAsOwner")
	List<JSONObject> getProjectListByUsernameAsOwner(@RequestParam("username") String username){
		List<JSONObject> list = kpiProjectOutService.getProjectListByUsernameAsOwner(username);
		return list;
	}

	@GetMapping("/getProjectListWithoutRequiredByUsernameAsOwner")
	List<JSONObject> getProjectListWithoutRequiredByUsernameAsOwner(@RequestParam("username") String username){
		List<JSONObject> list = kpiProjectOutService.getProjectListWithoutRequiredByUsernameAsOwner(username);
		return list;
	}

	@GetMapping("/getProjectByCode")
	JSONObject getProjectByCode(@RequestParam("project")String project){
		JSONObject list = kpiProjectOutService.getProjectByCode(project);
		return list;
	}

	@GetMapping("/getProjectTeamListByProject")
	List<JSONObject> getProjectTeamListByProject(@RequestParam("project")String project) {
		List<JSONObject> list = kpiProjectOutService.getProjectTeamListByProject(project);
		return list;
	}

}
