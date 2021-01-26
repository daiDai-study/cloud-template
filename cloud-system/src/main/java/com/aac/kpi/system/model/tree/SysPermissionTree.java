package com.aac.kpi.system.model.tree;

import com.aac.kpi.system.entity.SysPermission;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SysPermissionTree extends SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    private List<SysPermissionTree> children;

    private String menuType_dictText;

    public SysPermissionTree() {
    }

    public SysPermissionTree(SysPermission permission) {
        this.key = permission.getId();

        this.delFlag = permission.getDelFlag();
        this.createBy = permission.getCreateBy();
        this.createTime = permission.getCreateTime();
        this.updateBy = permission.getUpdateBy();
        this.updateTime = permission.getUpdateTime();

        this.setId(permission.getId());
        this.setParentId(permission.getParentId());
        this.setPerms(permission.getPerms());
        this.setComponent(permission.getComponent());
        this.setDescription(permission.getDescription());
        this.setIcon(permission.getIcon());
        this.setIsLeaf(permission.getIsLeaf());
        this.setMenuType(permission.getMenuType());
        this.setName(permission.getName());
        this.setSortNo(permission.getSortNo());
        this.setRedirect(permission.getRedirect());
        this.setUrl(permission.getUrl());
        this.setHidden(permission.isHidden());
        this.setAlwaysShow(permission.isAlwaysShow());

        if (!permission.getIsLeaf()) {
            this.children = new ArrayList<SysPermissionTree>();
        }
    }
}
