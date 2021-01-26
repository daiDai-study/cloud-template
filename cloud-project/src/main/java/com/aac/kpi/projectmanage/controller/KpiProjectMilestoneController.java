package com.aac.kpi.projectmanage.controller;

import com.aac.kpi.projectmanage.entity.KpiProjectMilestone;
import com.aac.kpi.projectmanage.service.IKpiProjectMilestoneService;
import com.aac.kpi.projectmanage.util.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: Controller
 * @Description: 项目里程碑事件表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@RestController
@RequestMapping("/kpiProjectMilestone")
@Slf4j
@Api("项目里程碑事件表")
public class KpiProjectMilestoneController {
	@Autowired
	private IKpiProjectMilestoneService kpiProjectMilestoneService;

	@GetMapping("/getProjectEventById")
	public Result<List<KpiProjectMilestone>> getProjectEventById(@RequestParam("id")Long id) {
		Result<List<KpiProjectMilestone>> result = new Result<>();
		List<KpiProjectMilestone> list = kpiProjectMilestoneService.getProjectEventById(id);
		result.setResult(list);
		result.success("获取信息成功");
		return result;
	}

	/**
	 * 创建事件
	 * @param kpiProjectMilestone
	 * @return
	 */
	@PostMapping("/createProjectEvent")
	public Result<String> createProjectEvent(@RequestBody KpiProjectMilestone kpiProjectMilestone) {
		Result<String> result = new Result<>();
		String ss = kpiProjectMilestoneService.createProjectEvent(kpiProjectMilestone);
		result.setResult(ss);
		result.success("创建事件成功");
		return result;
	}

    @PostMapping("/submitAllEvent")
    public Result<String> submitAllEvent(@RequestBody List<KpiProjectMilestone> kpiProjectMilestones) {
        Result<String> result = new Result<>();
        String ss = kpiProjectMilestoneService.submitAllEvent(kpiProjectMilestones);
        result.setResult(ss);
        result.success("更新事件成功");
        return result;
    }

    @GetMapping("/delById")
    public Result<String> delById(@RequestParam("id")Long id) {
        Result<String> result = new Result<>();
        String ss = kpiProjectMilestoneService.delById(id);
        result.setResult(ss);
        result.success("删除事件成功");
        return result;
    }

}
