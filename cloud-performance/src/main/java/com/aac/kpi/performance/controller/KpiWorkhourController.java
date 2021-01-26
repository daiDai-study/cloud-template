package com.aac.kpi.performance.controller;


import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.exception.AuthException;
import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.performance.api.project.ProjectClientWrapper;
import com.aac.kpi.performance.api.system.UserClientWrapper;
import com.aac.kpi.performance.entity.KpiWorkhour;
import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.aac.kpi.performance.service.KpiWorkhourService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/workhour")
@Slf4j
public class KpiWorkhourController extends BaseManagementController<KpiWorkhour, KpiWorkhourService> {

    @Resource
    public void setS(KpiWorkhourService service) {
        this.service = service;
    }

    @Resource
    private UserClientWrapper userClientWrapper;

    @Resource
    private ProjectClientWrapper projectClientWrapper;

    /**
     * 根据 参与月份 获取当前用户的项目列表
     *
     * @param theMonth 年月，如 2020-12
     * @return 项目列表(不包括必选项目)
     */
    @GetMapping("listProjectWithoutRequiredByMonth")
    public ApiResult listProjectWithoutRequiredByMonth(@RequestParam("theMonth") String theMonth) {
        String currentUsername = userClientWrapper.getCurrentUsername();
        List<KpiProjectModel> projectListByMonth = projectClientWrapper.listProjectWithoutRequiredByUsernameAndMonth(currentUsername, theMonth);
        return ApiResult.ofSuccess(projectListByMonth);
    }

    /**
     * 根据 参与月份 获取当前用户的项目列表
     *
     * @param theMonth 年月，如 2020-12
     * @return 项目列表(包括必选项目)
     */
    @GetMapping("listProjectWithRequiredByMonth")
    public ApiResult listProjectWithRequiredByMonth(@RequestParam("theMonth") String theMonth) {
        String currentUsername = userClientWrapper.getCurrentUsername();
        List<KpiProjectModel> projectListByMonth = projectClientWrapper.listProjectWithRequiredByUsernameAndMonth(currentUsername, theMonth);
        return ApiResult.ofSuccess(projectListByMonth);
    }

    /**
     * 根据 参与月份 获取当前用户作为 PO 的项目列表
     *
     * @param theMonth 年月，如 2020-12
     * @return 项目列表(不包括必选项目)
     */
    @GetMapping("listProjectWithoutRequiredByMonthAsOwner")
    public ApiResult listProjectWithoutRequiredByMonthAsOwner(@RequestParam("theMonth") String theMonth) {
        String currentUsername = userClientWrapper.getCurrentUsername();
        List<KpiProjectModel> projectListByMonth = projectClientWrapper.listProjectWithoutRequiredByUsernameAndMonthAsOwner(currentUsername, theMonth);
        return ApiResult.ofSuccess(projectListByMonth);
    }

    /**
     * 根据 参与月份 获取当前用户作为 PO 的项目列表
     *
     * @param theMonth 年月，如 2020-12
     * @return 项目列表(可能包括必选项目)
     */
    @GetMapping("listProjectByMonthAsOwner")
    public ApiResult listProjectByMonthAsOwner(@RequestParam("theMonth") String theMonth) {
        String currentUsername = userClientWrapper.getCurrentUsername();
        List<KpiProjectModel> projectListByMonth = projectClientWrapper.listProjectByUsernameAndMonthAsOwner(currentUsername, theMonth);
        return ApiResult.ofSuccess(projectListByMonth);
    }

    /**
     * 根据 参与月份 获取当前用户所有项目在参与月份的工时记录
     * 录入工时必须包括必选项目
     * @param theMonth 年月，如 2020-12
     * @return 当前用户所有项目在参与月份的工时记录
     */
    @GetMapping("listByMonth")
    public ApiResult listByMonthForInput(@RequestParam("theMonth") String theMonth) {
        String currentUsername = userClientWrapper.getCurrentUsername();
        List<KpiProjectModel> projectListByMonth = projectClientWrapper.listProjectWithRequiredByUsernameAndMonth(currentUsername, theMonth);
        List<KpiWorkhour> list = service.listByUsernameAndMonth(currentUsername, theMonth, projectListByMonth);
        JsonNode jsonNode = service.format(list, currentUsername, theMonth, projectListByMonth);
        return ApiResult.ofSuccess(jsonNode);
    }

    /**
     * 根据 参与月份 获取当前用户所有项目在参与月份的工时记录
     *
     * @param theMonth 年月，如 2020-12
     * @return 当前用户所有项目在参与月份的工时记录
     */
    @GetMapping("listForConfirm")
    public ApiResult listForConfirm(@RequestParam("theMonth") String theMonth, @RequestParam("project") String project, @RequestParam("theWeek") Integer theWeek) {
        String currentUsername = userClientWrapper.getCurrentUsername();
        List<KpiProjectTeamModel> kpiProjectTeamModelList = projectClientWrapper.getProjectTeamByProjectAndMonth(project, theMonth);
        // 当前用户必须是当前项目的负责人，否则没权限进行下一步
        if (!projectClientWrapper.isOwner(kpiProjectTeamModelList, currentUsername)) {
            log.info("‘{}’不是‘{}’项目在‘{}’月份中的负责人，因此没有权限进行‘工时确认’操作", currentUsername, project, theMonth);
            return ApiResult.ofFailClientUnauthorized();
        }
        List<KpiWorkhour> list = service.listForConfirm(currentUsername, theMonth, project, theWeek);
        JsonNode jsonNode = service.formatForConfirm(list, kpiProjectTeamModelList, theMonth, theWeek);
        return ApiResult.ofSuccess(jsonNode);
    }



    /**
     * 批量 添加或修改 当前用户在某些项目中的某些天数的工时记录
     *
     * @param list 项目-日期-工时的列表
     * @return 操作是否成功
     */
    @PostMapping("save")
    public ApiResult save(@RequestBody List<KpiWorkhour> list) {
        String currentUsername = userClientWrapper.getCurrentUsername();
        return ApiResult.ofSuccess(service.addOrEditBatch(currentUsername, list));
    }

    /**
     * 工时确认
     *
     * @param list 项目-日期-工时的列表
     * @return 操作是否成功
     */
    @PostMapping("confirm")
    public ApiResult confirm(@RequestBody List<KpiWorkhour> list) {
        String currentUsername = userClientWrapper.getCurrentUsername();
        return ApiResult.ofSuccess(service.confirm(currentUsername, list));
    }

    /**
     * 获取指定项目的所有工时记录
     * @param projects 多个项目
     * @return 所有工时记录
     */
    @GetMapping("listByProjects")
    public List<KpiWorkhour> listByProjects(@RequestParam("projects") List<String> projects){
        return service.listByProjects(projects);
    }
}
