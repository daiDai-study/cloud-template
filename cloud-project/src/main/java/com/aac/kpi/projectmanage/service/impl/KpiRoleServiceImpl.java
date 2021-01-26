package com.aac.kpi.projectmanage.service.impl;

import com.aac.kpi.projectmanage.entity.KpiRole;
import com.aac.kpi.projectmanage.mapper.KpiRoleMapper;
import com.aac.kpi.projectmanage.service.IKpiRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KpiRoleServiceImpl extends ServiceImpl<KpiRoleMapper, KpiRole> implements IKpiRoleService {
    @Override
    public List<Map<String, String>> getRoleToSelectModel() {
        List<Map<String, String>> list = new ArrayList<>();
        List<KpiRole> allRole = this.list();
        for (KpiRole kpiRole : allRole) {
            Map<String, String> map = new HashMap<>();
            map.put("value", kpiRole.getRole());
            map.put("label", kpiRole.getName());
            list.add(map);
        }

        return list;
    }
}
