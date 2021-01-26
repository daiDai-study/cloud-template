package com.aac.kpi.projectmanage.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aac.kpi.projectmanage.entity.KpiUser;
import com.aac.kpi.projectmanage.mapper.KpiUserMapper;
import com.aac.kpi.projectmanage.service.IKpiUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 人员信息表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
@Service
public class KpiUserServiceImpl extends ServiceImpl<KpiUserMapper, KpiUser> implements IKpiUserService {
    @Resource
    private KpiUserMapper kpiUserMapper;

    @Override
    public List<KpiUser> getByUserAccount(List<String> userAccounts) {
        QueryWrapper<KpiUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("usrid", userAccounts);
        return this.list(queryWrapper);
    }

    @Override
    public List<Map<String, String>> getAllKpiUserToSelectModel() {
        List<KpiUser> allUsers = this.list();
        List<Map<String, String>> list = new ArrayList<>();
        for (KpiUser allUser : allUsers) {
            Map<String, String> map = new HashMap<>();
            map.put("label", allUser.getSname());
            map.put("value", allUser.getUsrid());
            list.add(map);
        }
        return list;
    }

    @Override
    public KpiUser getByUserid(String userId) {
        QueryWrapper<KpiUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("usrid", userId);
        List<KpiUser> list = this.list(queryWrapper);
        return CollectionUtil.isNotEmpty(list)?list.get(0):null;
    }
}
