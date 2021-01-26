package com.aac.kpi.projectmanage.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ProjectProcessDto implements Serializable {

    /**
     * 序列化时保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 给前端用的
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;


    private String project;

    /**
     * 最近更新事件：2020-10-01格式
     */
    private String updateTime;

    /**
     * 更新人姓名
     */
    private String updateUserName;

    private List<Point> children;

    /**
     * 实际进度
     */
//    private List<Map<String, Object>> actualProcess;
//
//    /**
//     * 计划进度
//     */
//    private List<Map<String, Object>> planProcess;

    @Data
    public static class Point{
        private String actuTime;
        private String planTime;
        private String stageShowName;
        private String user;
        private String desc;
    }
}
