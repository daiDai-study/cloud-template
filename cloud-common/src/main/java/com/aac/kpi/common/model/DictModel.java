package com.aac.kpi.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DictModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典项值
     */
    private String value;
    /**
     * 字典项文本
     */
    private String text;

    private String label;

    public void setText(String text) {
        this.text = text;
        this.label = text;
    }

    public void setLabel(String label) {
        this.label = label;
        this.text = label;
    }

    public DictModel() {
    }

    public DictModel(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
