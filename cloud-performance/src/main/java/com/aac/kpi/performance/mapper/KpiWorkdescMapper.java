package com.aac.kpi.performance.mapper;

import com.aac.kpi.performance.entity.KpiWorkdesc;
import com.aac.kpi.performance.entity.KpiWorkhour;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KpiWorkdescMapper extends BaseMapper<KpiWorkdesc> {

    KpiWorkdesc getByCompositeUnique(@Param("username") String username, @Param("project") String project, @Param("theMonth") String theMonth);

    List<KpiWorkdesc> listByProjectAndMonth(@Param("project") String project, @Param("theMonth") String theMonth);
}
