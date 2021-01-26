package com.aac.kpi.projectmanage.controller;

import com.aac.kpi.projectmanage.service.IKpiUserService;
import com.aac.kpi.projectmanage.util.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Title: Controller
 * @Description: 人员信息表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@RestController
@RequestMapping("/kpiUser")
@Slf4j
@Api("人员信息表")
public class KpiUserController {
	@Autowired
	private IKpiUserService kpiUserService;

	@GetMapping("/getAllKpiUserToSelectModel")
	public Result<List<Map<String, String>>> getAllKpiUserToSelectModel() {
		Result<List<Map<String, String>>> result = new Result<>();
		List<Map<String, String>> list = kpiUserService.getAllKpiUserToSelectModel();
		result.setResult(list);
		result.success("获取信息成功");
		return result;
	}

}
