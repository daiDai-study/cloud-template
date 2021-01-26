package com.aac.kpi.projectmanage.constant;

import com.aac.kpi.projectmanage.entity.KpiRole;
import com.aac.kpi.projectmanage.service.IKpiRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class RoleConstant {

    @Autowired
    private IKpiRoleService kpiRoleService;

    public static final String ROLE_ARCH = "ARCH";
    public static final String ROLE_SM = "SM";
    public static final String ROLE_PO = "PO";
    public static final String ROLE_BT = "BT";
    public static final String ROLE_UI = "UI";
    public static final String ROLE_DE = "DE";
    public static final String ROLE_DEV = "DEV";

    @PostConstruct
    public void init() {
        List<KpiRole> roles = kpiRoleService.list();

    }


}
