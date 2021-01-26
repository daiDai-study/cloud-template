package com.aac.kpi.projectmanage.service;

import com.aac.kpi.projectmanage.entity.KpiProjectValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 项目每周价值达成表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
public interface IKpiProjectValueService extends IService<KpiProjectValue> {


    List<KpiProjectValue> getProjectValueByProjectId(Long id);

    String createOrUpdateProjectValue(List<KpiProjectValue> values);

    String delById(Long id);

    Map<String, List<Object>> getChartsDataByProjectId(Long id);
}
