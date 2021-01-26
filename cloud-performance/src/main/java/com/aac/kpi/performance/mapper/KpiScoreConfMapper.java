package com.aac.kpi.performance.mapper;

import com.aac.kpi.performance.entity.KpiScoreConf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface KpiScoreConfMapper extends BaseMapper<KpiScoreConf> {

    String getRankByScore(@Param("score") Integer score);
}
