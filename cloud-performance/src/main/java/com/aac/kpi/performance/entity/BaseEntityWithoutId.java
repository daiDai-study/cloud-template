package com.aac.kpi.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class BaseEntityWithoutId implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 删除状态（0，正常，1已删除）
     */
    public Boolean delFlag;
    /**
     * 创建人
     */
    public String createBy;
    /**
     * 创建时间
     */
    public Date createTime;
    /**
     * 更新人
     */
    public String updateBy;
    /**
     * 更新时间
     */
    public Date updateTime;
}
