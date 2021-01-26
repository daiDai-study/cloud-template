package com.aac.kpi.performance.controller;


import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.common.model.DictModel;
import com.aac.kpi.performance.service.KpiCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/calendar")
@Slf4j
public class KpiCalendarController {

    @Resource
    private KpiCalendarService service;

    @GetMapping("/listWeekByMonth")
    public ApiResult listWeekByMonth(@RequestParam("theMonth") String theMonth){
        List<DictModel> weeks = service.listWeekByMonth(theMonth);
        return ApiResult.ofSuccess(weeks);
    }
}
