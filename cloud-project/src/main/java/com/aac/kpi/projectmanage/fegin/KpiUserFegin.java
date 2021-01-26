package com.aac.kpi.projectmanage.fegin;

import com.aac.kpi.projectmanage.dto.SysUser;
import com.aac.kpi.projectmanage.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * kpi系统管理接口
 */
@FeignClient("kpi-system")
public interface KpiUserFegin {

    @GetMapping(value = "/system/user/getSubject")
    Result<SysUser> getSubject();

}
