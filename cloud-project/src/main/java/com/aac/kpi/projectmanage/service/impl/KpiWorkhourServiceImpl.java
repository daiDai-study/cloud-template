package com.aac.kpi.projectmanage.service.impl;

import com.aac.kpi.projectmanage.entity.KpiWorkhour;
import com.aac.kpi.projectmanage.mapper.KpiWorkhourMapper;
import com.aac.kpi.projectmanage.service.IKpiWorkhourService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 项目工时记录表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@Service
public class KpiWorkhourServiceImpl extends ServiceImpl<KpiWorkhourMapper, KpiWorkhour> implements IKpiWorkhourService {
    @Resource
    private KpiWorkhourMapper kpiWorkhourMapper;

//        @Override
//        public List<KpiWorkhour> getByProject(List<String> projects) {
//                QueryWrapper<KpiWorkhour> queryWrapper = new QueryWrapper<>();
//                queryWrapper.in("project", projects);
//                return this.list(queryWrapper);
//        }
//
//        @Override
//        public List<KpiWorkhour> getBySingleProject(String project) {
//                QueryWrapper<KpiWorkhour> queryWrapper = new QueryWrapper<>();
//                queryWrapper.eq("project", project);
//                return this.list(queryWrapper);
//        }
}
