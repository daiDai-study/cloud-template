package com.aac.kpi.system.model.tree;

import com.aac.kpi.system.entity.SysPermission;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class TreeModel implements Serializable {

    private static final long serialVersionUID = 1;

    private List<TreeModel> children;

    private String icon;

    private Boolean isLeaf;

    private String key;

    private String label;

    private String parentId;

    private String title;

    private String value;

    public TreeModel(SysPermission permission) {
        this.key = permission.getId();
        this.icon = permission.getIcon();
        this.parentId = permission.getParentId();
        this.title = permission.getName();
        this.value = permission.getId();
        this.isLeaf = permission.getIsLeaf();
        if (!permission.getIsLeaf()) {
            this.children = new ArrayList<TreeModel>();
        }
    }
}
