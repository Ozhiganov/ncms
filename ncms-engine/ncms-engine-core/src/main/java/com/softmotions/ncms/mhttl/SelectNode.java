package com.softmotions.ncms.mhttl;

/**
* @author Adamansky Anton (adamansky@gmail.com)
*/
public final class SelectNode {

    final String key;

    final String value;

    final boolean selected;

    public SelectNode(String key, String value, boolean selected) {
        this.key = key;
        this.value = value;
        this.selected = selected;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isSelected() {
        return selected;
    }
}