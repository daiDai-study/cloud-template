package com.aac.kpi.performance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.exception.BizException;
import com.aac.kpi.performance.entity.KpiScoreConf;
import com.aac.kpi.performance.mapper.KpiScoreConfMapper;
import com.aac.kpi.performance.service.KpiScoreConfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class KpiScoreConfServiceImpl extends ServiceImpl<KpiScoreConfMapper, KpiScoreConf> implements KpiScoreConfService {

    @Override
    public String getRankByScore(Integer score) {
        String rank = baseMapper.getRankByScore(score);
        if(StrUtil.isEmpty(rank)){
            throw new BizException("分数(" + score + ")没有对应的等级");
        }
        return rank;
    }
}
