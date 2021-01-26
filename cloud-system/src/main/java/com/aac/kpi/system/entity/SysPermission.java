package com.aac.kpi.system.entity;

import com.aac.kpi.system.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 菜单权限表
 * </p>
 *
 * @author ahdkkyxq
 * @since 2018-12-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysPermission extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父id
     */
    private String parentId;
    /**
     * 类型（0：一级菜单；1：子菜单 ；2：按钮权限）
     */
    @Dict("menu_type")
    private Integer menuType;

    /**
     * alwaysShow
     */
    private boolean alwaysShow;
    /**
     * 组件
     */
    private String component;
    /**
     * 描述
     */
    private String description;
    /**
     * 是否隐藏路由菜单: 0否,1是（默认值0）
     */
    private boolean hidden;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 是否叶子节点: 1:是 0:不是
     */
    private Boolean isLeaf;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单权限编码，例如：“sys:schedule:list,sys:schedule:info”,多个逗号隔开
     */
    private String perms;
    /**
     * 一级菜单跳转地址
     */
    private String redirect;
    /**
     * 菜单排序
     */
    private Integer sortNo;
    /**
     * 路径
     */
    private String url;
}
