package com.aac.kpi.performance.mapper;

import com.aac.kpi.performance.entity.KpiReviewTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KpiReviewTeamMapper extends BaseMapper<KpiReviewTeam> {

    KpiReviewTeam getByCompositeUnique(@Param("assessee") String assessee, @Param("assessor") String assessor, @Param("project") String project, @Param("theMonth") String theMonth);

    List<KpiReviewTeam> listByProjectAndMonth(@Param("project") String project, @Param("theMonth") String theMonth);
}
