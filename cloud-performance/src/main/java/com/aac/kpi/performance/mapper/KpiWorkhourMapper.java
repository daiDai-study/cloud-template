package com.aac.kpi.performance.mapper;

import com.aac.kpi.performance.entity.KpiWorkhour;
import com.aac.kpi.performance.model.KpiProjectModel;
import com.aac.kpi.performance.model.KpiProjectTeamModel;
import com.aac.kpi.performance.model.dto.MonthlyWorkhourDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface KpiWorkhourMapper extends BaseMapper<KpiWorkhour> {

    KpiWorkhour getByCompositeUnique(@Param("username") String username, @Param("project") String project, @Param("theDate") String theDate);

    List<KpiWorkhour> listByMonthAndProjectAndWeek(@Param("theMonth") String theMonth, @Param("project") String project, @Param("theWeek") Integer theWeek);

    List<KpiWorkhour> listByProjects(@Param("projects") List<String> projects);

    List<MonthlyWorkhourDTO> listMonthlyWorkhourByProjectAndMonth(@Param("theMonth") String theMonth, @Param("project") String project);

    BigDecimal getWorkhourMonthly(@Param("username") String username, @Param("theMonth") String theMonth, @Param("project") String project);

    BigDecimal getWorkhourConfirmedMonthly(@Param("username") String username, @Param("theMonth") String theMonth, @Param("project") String project);
}
