package com.aac.kpi.performance.mapper;

import com.aac.kpi.performance.entity.KpiCalendar;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KpiCalendarMapper extends BaseMapper<KpiCalendar> {

    List<Integer> listWeekByMonth(@Param("theMonth") String theMonth);

    List<String> listDateByMonthAndWeek(@Param("theMonth") String theMonth, @Param("theWeek") Integer theWeek);
}
