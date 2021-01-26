package com.aac.kpi.projectmanage.service.impl;

import com.aac.kpi.projectmanage.entity.KpiCalendar;
import com.aac.kpi.projectmanage.mapper.KpiCalendarMapper;
import com.aac.kpi.projectmanage.service.IKpiCalendarService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 日历表
 * @author： xujie
 * @date： 2020-12-21
 * @version： V1.0
 */
@Service
public class KpiCalendarServiceImpl extends ServiceImpl<KpiCalendarMapper, KpiCalendar> implements IKpiCalendarService {
    @Resource
    private KpiCalendarMapper kpiCalendarMapper;
}
