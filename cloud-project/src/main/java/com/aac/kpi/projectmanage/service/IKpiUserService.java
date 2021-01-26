package com.aac.kpi.projectmanage.service;

import com.aac.kpi.projectmanage.entity.KpiUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 人员信息表
 * @author： xujie
 * @date：   2020-12-21
 * @version： V1.0
 */
public interface IKpiUserService extends IService<KpiUser> {

    /**
     * 根据用户域账号获取用户信息
     * @param userAccounts
     * @return
     */
    List<KpiUser> getByUserAccount(List<String> userAccounts);

    /**
     * 获取所有kpi User，并且组装成前端下拉框需要的格式
     * @return
     */
    List<Map<String, String>> getAllKpiUserToSelectModel();

    /**
     * 根据域账号获取人员信息
     * @param userId
     * @return
     */
    KpiUser getByUserid(String userId);

}
