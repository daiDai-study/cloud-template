package com.aac.kpi.performance.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aac.kpi.common.model.DictModel;
import com.aac.kpi.performance.entity.KpiCalendar;
import com.aac.kpi.performance.mapper.KpiCalendarMapper;
import com.aac.kpi.performance.service.KpiCalendarService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class KpiCalendarServiceImpl extends ServiceImpl<KpiCalendarMapper, KpiCalendar> implements KpiCalendarService {

    @Override
    public List<KpiCalendar> listByDateList(Set<String> dateList) {
        if(CollUtil.isEmpty(dateList)){
            return new ArrayList<>();
        }
        return this.list(new QueryWrapper<KpiCalendar>().in("the_date", dateList));
    }

    @Override
    public List<KpiCalendar> listByMonth(String theMonth) {
        return this.list(new QueryWrapper<KpiCalendar>().eq("the_month", theMonth));
    }

    @Override
    public List<KpiCalendar> listByMonthWithoutSunday(String theMonth) {
        List<KpiCalendar> kpiCalendars = listByMonth(theMonth);
        kpiCalendars = kpiCalendars.stream().filter(kpiCalendar -> kpiCalendar.getTheWeekday() != 7).collect(Collectors.toList());
        return kpiCalendars;
    }

    @Override
    public List<DictModel> listWeekByMonth(String theMonth) {
        List<DictModel> dictModelList = new ArrayList<>();
        List<Integer> weeks = baseMapper.listWeekByMonth(theMonth);
        for (Integer week : weeks) {
            String value = week.toString();
            String yearStr = value.substring(0, 4);
            String weekStr = value.substring(4);
            dictModelList.add(new DictModel(value, yearStr + "年" + weekStr + "周"));
        }
        return dictModelList;
    }

    @Override
    public List<String> listDateByMonthAndWeek(String theMonth, Integer theWeek) {
        return baseMapper.listDateByMonthAndWeek(theMonth, theWeek);
    }
}
