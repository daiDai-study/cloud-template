package com.aac.kpi.performance.controller;


import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.exception.AuthException;
import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.performance.api.project.ProjectClientWrapper;
import com.aac.kpi.performance.api.system.UserClientWrapper;
import com.aac.kpi.performance.entity.KpiReviewTeam;
import com.aac.kpi.performance.entity.KpiScoreConf;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.aac.kpi.performance.service.KpiReviewTeamService;
import com.aac.kpi.performance.service.KpiScoreConfService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/scoreConf")
@Slf4j
public class KpiScoreConfController extends BaseManagementController<KpiScoreConf, KpiScoreConfService> {

    @Resource
    public void setS(KpiScoreConfService service){
        this.service = service;
    }

    @Resource
    private UserClientWrapper userClientWrapper;

    @Resource
    private ProjectClientWrapper projectClientWrapper;

    @GetMapping("/getRankByScore")
    public ApiResult getRankByScore(@RequestParam("score") Integer score){
        String rank = service.getRankByScore(score);
        return ApiResult.ofSuccess(rank);
    }
}
