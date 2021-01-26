package com.aac.kpi.projectmanage.dto;

import com.aac.kpi.projectmanage.entity.KpiProject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class KpiProjectDto extends KpiProject {

    /**
     * 项目人员数
     */
    private Integer projectUserNum;

    /**
     * 项目成本，根据工时算出来的
     */
    private Float projectCost = 0F;

    /**
     * 当月累计成本
     */
    private Float currentMonthProjectCost = 0F;

    /**
     * 项目已经进行了几天了
     */
    private Integer projectUseDay;

    /**
     * 项目用户信息，包括角色
     */
    private List<String> userRoleInfo = new ArrayList<>();

    /**
     * 柱状图用户数据
     */
    private List<ColumnUserData> columnUserDatas;

    /**
     * 柱状图横坐标
     */
    private List<Integer> dayCategories;

    /**
     * 按月统计时，横坐标
     */
    private List<String> monthCategories;

    /**
     * 柱状图每个柱子成本汇总
     */
    private List<Float> columnCostSum;

    /**
     * 项目是否结束：进行中，已结束
     * 根据项目结束日期来判断
     */
//    private String projectStatus;

    /**
     * 累计项目时长
     */
    private Integer sumProjectHour = 0;

    /**
     * 当月项目时长
     */
    private Integer currentMonthHour = 0;

    /**
     * 累计项目价值达成
     */
    private Long sumProjectValue = 0L;


    @Data
    public static class ColumnUserData {
        private String name;
        private List<Float> data;
    }

}
