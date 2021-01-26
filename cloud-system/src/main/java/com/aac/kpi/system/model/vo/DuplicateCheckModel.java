package com.aac.kpi.system.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DuplicateCheckModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段值
     */
    private String fieldVal;

    /**
     * 数据ID
     */
    private String dataId;

}
