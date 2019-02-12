package com.osc.skyz.zkviewer.entity;

/**
 * 功能描述:
 * <p/>
 * 创建人: shoujun.yang
 * <p/>
 * 创建时间: 2019/1/29 5:14 PM.
 * <p/>
 */
public class TreeNode {

    private String id;

    private String text;

    private String state = "closed";

    public String getId() {
        return id;
    }

    public TreeNode setId(String id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public TreeNode setText(String text) {
        this.text = text;
        return this;
    }

    public String getState() {
        return state;
    }

    public TreeNode setState(String state) {
        this.state = state;
        return this;
    }
}
