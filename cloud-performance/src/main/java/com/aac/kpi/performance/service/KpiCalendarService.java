package com.aac.kpi.performance.service;

import com.aac.kpi.common.model.DictModel;
import com.aac.kpi.performance.entity.KpiCalendar;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

public interface KpiCalendarService extends IService<KpiCalendar> {

    List<KpiCalendar> listByDateList(Set<String> dateList);

    List<KpiCalendar> listByMonth(String theMonth);

    List<KpiCalendar> listByMonthWithoutSunday(String theMonth);

    List<DictModel> listWeekByMonth(String theMonth);

    List<String> listDateByMonthAndWeek(String theMonth, Integer theWeek);
}
