package com.aac.kpi.performance.service;

import com.aac.kpi.performance.entity.KpiReviewTeam;
import com.aac.kpi.performance.entity.KpiScoreConf;
import com.baomidou.mybatisplus.extension.service.IService;

public interface KpiScoreConfService extends IService<KpiScoreConf> {

    String getRankByScore(Integer score);
}
