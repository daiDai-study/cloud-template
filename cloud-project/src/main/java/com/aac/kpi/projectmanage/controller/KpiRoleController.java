package com.aac.kpi.projectmanage.controller;

import com.aac.kpi.projectmanage.service.IKpiRoleService;
import com.aac.kpi.projectmanage.util.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kpiRole")
@Slf4j
@Api("角色信息")
public class KpiRoleController {

    @Autowired
    private IKpiRoleService kpiRoleService;

    @GetMapping("/getRoleToSelectModel")
    public Result<List<Map<String, String>>> getRoleToSelectModel() {
        Result<List<Map<String, String>>> result = new Result<>();
        List<Map<String, String>> list = kpiRoleService.getRoleToSelectModel();
        result.setResult(list);
        result.success("获取信息成功");
        return result;
    }

}
