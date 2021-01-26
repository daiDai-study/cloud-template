package com.aac.kpi.system.model.tree;

import lombok.Data;

import java.io.Serializable;

@Data
public class TreeSelectModel implements Serializable {

    private static final long serialVersionUID = 1;

    private String icon;

    private boolean isLeaf;

    private String key;

    private String parentId;

    private String title;

    private String value;
}
