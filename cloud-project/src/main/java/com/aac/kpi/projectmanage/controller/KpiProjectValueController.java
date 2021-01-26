package com.aac.kpi.projectmanage.controller;

import com.aac.kpi.projectmanage.entity.KpiProjectValue;
import com.aac.kpi.projectmanage.service.IKpiProjectValueService;
import com.aac.kpi.projectmanage.util.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Title: Controller
 * @Description: 项目每周价值达成表
 * @author： xujie
 * @date：   2021-01-19
 * @version： V1.0
 */
@RestController
@RequestMapping("/kpiProjectValue")
@Slf4j
@Api("项目每周价值达成表")
public class KpiProjectValueController {
	@Autowired
	private IKpiProjectValueService kpiProjectValueService;

	@GetMapping("/getProjectValueByProjectId")
	public Result<List<KpiProjectValue>> getProjectValueByProjectId(@RequestParam("id")Long id) {
		Result<List<KpiProjectValue>> result = new Result<>();
		List<KpiProjectValue> list = kpiProjectValueService.getProjectValueByProjectId(id);
		result.setResult(list);
		result.success("获取信息成功");
		return result;
	}

	@PostMapping("/createOrUpdateProjectValue")
	public Result<String> createOrUpdateProjectValue(@RequestBody List<KpiProjectValue> values) {
		Result<String> result = new Result<>();
		String ss = kpiProjectValueService.createOrUpdateProjectValue(values);
		result.setResult(ss);
		result.success("价值更新成功");
		return result;
	}

	@GetMapping("/delById")
	public Result<String> delById(@RequestParam("id")Long id) {
		Result<String> result = new Result<>();
		String ss = kpiProjectValueService.delById(id);
		result.setResult(ss);
		result.success("删除成功");
		return result;
	}

	@GetMapping("/getChartsDataByProjectId")
	public Result<Map<String, List<Object>>> getChartsDataByProjectId(@RequestParam("id")Long id) {
		Result<Map<String, List<Object>>> result = new Result<>();
		Map<String, List<Object>> map = kpiProjectValueService.getChartsDataByProjectId(id);
		result.setResult(map);
		result.success("获取信息成功");
		return result;
	}

}
