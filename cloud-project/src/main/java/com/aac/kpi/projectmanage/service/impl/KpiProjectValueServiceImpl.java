package com.aac.kpi.projectmanage.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aac.kpi.projectmanage.dto.SysUser;
import com.aac.kpi.projectmanage.entity.KpiProjectValue;
import com.aac.kpi.projectmanage.fegin.KpiUserFegin;
import com.aac.kpi.projectmanage.mapper.KpiProjectValueMapper;
import com.aac.kpi.projectmanage.service.IKpiProjectService;
import com.aac.kpi.projectmanage.service.IKpiProjectValueService;
import com.aac.kpi.projectmanage.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description: 项目里程碑事件表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@Service
public class KpiProjectValueServiceImpl extends ServiceImpl<KpiProjectValueMapper, KpiProjectValue> implements IKpiProjectValueService {
    @Resource
    private KpiProjectValueMapper kpiProjectValueMapper;
    @Autowired
    private IKpiProjectService kpiProjectService;
    @Autowired
    private KpiUserFegin kpiUserFegin;


    @Override
    public List<KpiProjectValue> getProjectValueByProjectId(Long id) {
        QueryWrapper<KpiProjectValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", id);
        return this.list(queryWrapper);
    }

    @Override
    public String createOrUpdateProjectValue(List<KpiProjectValue> values) {
        if (CollectionUtil.isEmpty(values)) {
            return "0";
        }

        Result<SysUser> subject = kpiUserFegin.getSubject();
        SysUser currentUser = subject.getData();

        for (KpiProjectValue value : values) {
            if (value.getId() == null) {
                // 创建
                value.setCreateTime(new Date());
                value.setCreateBy(currentUser.getUsername());
                value.setUpdateBy(currentUser.getUsername());
                value.setUpdateTime(new Date());
                this.save(value);
            } else {
                // 更新
                value.setUpdateBy(currentUser.getUsername());
                value.setUpdateTime(new Date());
                this.updateById(value);
            }
        }

        return "1";
    }

    @Override
    public String delById(Long id) {
        this.removeById(id);
        return "1";
    }

    @Override
    public Map<String, List<Object>> getChartsDataByProjectId(Long id) {
        List<KpiProjectValue> values = this.getProjectValueByProjectId(id);
        Map<String, List<Object>> map = new HashMap<>();
        List<Object> weekInfo = new ArrayList<>();
        List<Object> singleValue = new ArrayList<>();
        List<Object> sumValue = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(values)) {
            int sumWeekValue = 0;
            for (KpiProjectValue value : values) {
                String yearWeek = value.getYear() + "-" + value.getWeek() + "周";
                weekInfo.add(yearWeek);

                int weekValue = value.getValue() == null?0:value.getValue();
                singleValue.add(weekValue);

                sumWeekValue += weekValue;
                sumValue.add(sumWeekValue);
            }
        }
        map.put("weekInfo", weekInfo);
        map.put("singleValue", singleValue);
        map.put("sumValue", sumValue);
        return map;
    }
}
