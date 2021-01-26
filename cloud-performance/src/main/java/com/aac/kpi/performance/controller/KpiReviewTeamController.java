package com.aac.kpi.performance.controller;


import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.exception.AuthException;
import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.performance.api.project.ProjectClientWrapper;
import com.aac.kpi.performance.api.system.UserClientWrapper;
import com.aac.kpi.performance.entity.KpiReviewTeam;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.aac.kpi.performance.service.KpiReviewTeamService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/reviewTeam")
@Slf4j
public class KpiReviewTeamController extends BaseManagementController<KpiReviewTeam, KpiReviewTeamService> {

    @Resource
    public void setS(KpiReviewTeamService service){
        this.service = service;
    }

    @Resource
    private UserClientWrapper userClientWrapper;

    @Resource
    private ProjectClientWrapper projectClientWrapper;

    /**
     * 根据 参与月份和项目 获取当前用户在所有成员互评
     *
     * @param theMonth 年月，如 2020-12
     * @param project 项目
     * @return 成员互评
     */
    @GetMapping("listByMonthAndProject")
    public ApiResult listByMonth(@RequestParam("theMonth") String theMonth, @RequestParam("project") String project) {
        String currentUsername = userClientWrapper.getCurrentUsername();
        List<KpiProjectTeamModel> kpiProjectTeamModelList = projectClientWrapper.getProjectTeamByProjectAndMonthWithoutUsername(project, currentUsername, theMonth);
        List<KpiReviewTeam> list = service.listByUsernameAndMonth(currentUsername, theMonth, kpiProjectTeamModelList);
        JsonNode jsonNode = service.format(list, currentUsername, theMonth, kpiProjectTeamModelList);
        return ApiResult.ofSuccess(jsonNode);
    }

    /**
     * 批量 修改 成员互评
     * @param list 项目-月份-被评人-互评信息的列表
     * @return 操作是否成功
     */
    @PostMapping("save")
    public ApiResult save(@RequestBody List<KpiReviewTeam> list){
        String currentUsername = userClientWrapper.getCurrentUsername();
        return ApiResult.ofSuccess(service.addOrEditBatch(currentUsername, list));
    }
}
