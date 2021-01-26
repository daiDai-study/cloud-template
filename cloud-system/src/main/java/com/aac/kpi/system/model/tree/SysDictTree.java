package com.aac.kpi.system.model.tree;

import com.aac.kpi.system.entity.SysDict;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysDictTree extends SysDict implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    private String title;

    public SysDictTree(SysDict node) {
        this.key = node.getId();
        this.title = node.getDictName();
        this.delFlag = node.getDelFlag();
        this.createBy = node.getCreateBy();
        this.createTime = node.getCreateTime();
        this.updateBy = node.getUpdateBy();
        this.updateTime = node.getUpdateTime();

        this.setId(node.getId());
        this.setDictCode(node.getDictCode());
        this.setDictName(node.getDictName());
        this.setDescription(node.getDescription());
    }

}
