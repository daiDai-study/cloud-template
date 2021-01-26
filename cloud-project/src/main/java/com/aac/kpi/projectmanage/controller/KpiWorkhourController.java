package com.aac.kpi.projectmanage.controller;

import com.aac.kpi.projectmanage.entity.KpiWorkhour;
import com.aac.kpi.projectmanage.fegin.KpiWorkhourFegin;
import com.aac.kpi.projectmanage.service.IKpiWorkhourService;
import com.aac.kpi.projectmanage.util.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: Controller
 * @Description: 项目工时记录表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@RestController
@RequestMapping("/kpiWorkhour")
@Slf4j
@Api("项目工时记录表")
public class KpiWorkhourController {
	@Autowired
	private IKpiWorkhourService kpiWorkhourService;
	@Autowired
	private KpiWorkhourFegin kpiWorkhourFegin;

	@GetMapping("/testFegin")
	public List<KpiWorkhour> testFegin() {
		List<String> list = new ArrayList<>();
		list.add("PLP");
		List<KpiWorkhour> kpiWorkhours = kpiWorkhourFegin.listByProjects(list);
		return kpiWorkhours;
	}

}
