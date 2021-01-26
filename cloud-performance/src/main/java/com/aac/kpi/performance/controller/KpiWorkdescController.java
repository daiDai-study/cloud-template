package com.aac.kpi.performance.controller;


import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.exception.AuthException;
import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.performance.api.project.ProjectClientWrapper;
import com.aac.kpi.performance.api.system.UserClientWrapper;
import com.aac.kpi.performance.entity.KpiWorkdesc;
import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.aac.kpi.performance.model.vo.KpiTeamMemberMonthlyInfoVO;
import com.aac.kpi.performance.service.KpiWorkdescService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/workdesc")
@Slf4j
public class KpiWorkdescController extends BaseManagementController<KpiWorkdesc, KpiWorkdescService> {

    @Resource
    public void setS(KpiWorkdescService service){
        this.service = service;
    }

    @Resource
    private UserClientWrapper userClientWrapper;

    @Resource
    private ProjectClientWrapper projectClientWrapper;

    /**
     * 根据 参与月份 获取当前用户所有项目在参与月份的月度绩效描述
     * 填写月度绩效描述包括必选项目
     * @param theMonth 年月，如 2020-12
     * @return 当前用户所有项目在参与月份的月度绩效描述
     */
    @GetMapping("listByMonth")
    public ApiResult listByMonth(@RequestParam("theMonth") String theMonth) {
        String currentUsername = userClientWrapper.getCurrentUsername();
        List<KpiProjectModel> projectListByMonth = projectClientWrapper.listProjectWithRequiredByUsernameAndMonth(currentUsername, theMonth);
        List<KpiWorkdesc> list = service.listByUsernameAndMonth(currentUsername, theMonth, projectListByMonth);
        JsonNode jsonNode = service.format(list, currentUsername, theMonth, projectListByMonth);
        return ApiResult.ofSuccess(jsonNode);
    }

    /**
     * 批量 添加或修改 当前用户在某些项目中的月度绩效描述
     * @param list 项目-月份-月度绩效描述的列表
     * @return 操作是否成功
     */
    @PostMapping("save")
    public ApiResult save(@RequestBody List<KpiWorkdesc> list){
        String currentUsername = userClientWrapper.getCurrentUsername();
        return ApiResult.ofSuccess(service.addOrEditBatch(currentUsername, list));
    }

    /**
     * 确认考核分数和等级
     * @param theMonth 年月，如 2020-12
     * @param project 项目
     * @return 考核前的所有成员的工时汇总信息、月度绩效描述、成员互评信息和考核分数等级
     */
    @GetMapping("listForScoring")
    public ApiResult listForScoring(@RequestParam("theMonth") String theMonth, @RequestParam("project") String project) {
        String currentUsername = userClientWrapper.getCurrentUsername();
        List<KpiProjectTeamModel> kpiProjectTeamModelList = projectClientWrapper.getProjectTeamByProjectAndMonth(project, theMonth);
        // 当前用户必须是当前项目的负责人，否则没权限进行下一步
        if (!projectClientWrapper.isOwner(kpiProjectTeamModelList, currentUsername)) {
            log.info("‘{}’不是‘{}’项目在‘{}’月份中的负责人，因此没有权限进行‘PO确认’的操作", currentUsername, project, theMonth);
            return ApiResult.ofFailClientUnauthorized();
        }
        List<KpiTeamMemberMonthlyInfoVO> list = service.listForScoring(currentUsername, theMonth, project, kpiProjectTeamModelList);
        return ApiResult.ofSuccess(list);
    }

    /**
     * 确认考核分数和等级
     * @param list 项目-月份-成员-月度绩效描述的列表
     * @return 操作是否成功
     */
    @PostMapping("confirmScore")
    public ApiResult confirmScore(@RequestBody List<KpiWorkdesc> list){
        String currentUsername = userClientWrapper.getCurrentUsername();
        return ApiResult.ofSuccess(service.confirmScore(currentUsername, list));
    }
}
