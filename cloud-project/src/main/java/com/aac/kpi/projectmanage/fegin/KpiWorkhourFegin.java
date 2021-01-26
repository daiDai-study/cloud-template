package com.aac.kpi.projectmanage.fegin;

import com.aac.kpi.projectmanage.entity.KpiWorkhour;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 获取工时微服务的接口
 */
@FeignClient("kpi-performance")
public interface KpiWorkhourFegin {

    @GetMapping("/performance/workhour/listByProjects")
    List<KpiWorkhour> listByProjects(@RequestParam("projects") List<String> projects);

}
