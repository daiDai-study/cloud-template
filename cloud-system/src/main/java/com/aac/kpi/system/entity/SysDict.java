package com.aac.kpi.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 数据字典
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDict extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    private String dictCode;
    /**
     * 字典名称
     */
    private String dictName;
    /**
     * 描述
     */
    private String description;
}
