package com.aac.kpi.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 数据字典项
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictItem extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典id
     */
    private String dictId;

    /**
     * 字典项值
     */
    private String itemValue;
    /**
     * 字典项文本
     */
    private String itemText;
    /**
     * 排序
     */
    private BigDecimal sortOrder;
    /**
     * 状态（1启用 0不启用）
     */
    private Integer status;

    /**
     * 邮件模板
     */
    private String mailContent;
    /**
     * 描述
     */
    private String description;
}
