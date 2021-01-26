package com.aac.kpi.projectmanage.controller;

import com.aac.kpi.projectmanage.service.IKpiCalendarService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Title: Controller
 * @Description: 日历表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@RestController
@RequestMapping("/kpiCalendar")
@Slf4j
@Api("日历表")
public class KpiCalendarController {
	@Autowired
	private IKpiCalendarService kpiCalendarService;


}
