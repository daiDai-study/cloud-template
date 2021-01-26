package com.aac.kpi.projectmanage.service;

import com.aac.kpi.projectmanage.entity.KpiRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IKpiRoleService extends IService<KpiRole> {

    List<Map<String, String>> getRoleToSelectModel();
}
