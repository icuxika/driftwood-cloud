package com.icuxika.util;

public class MetaTreeNode<T> extends TreeNode<T> {

    /**
     * 树的深度，从1开始
     */
    private Integer depth;

    private MetaTreeNode<T> parentNode;

    public MetaTreeNode(Integer depth, MetaTreeNode<T> parentNode) {
        this.depth = depth;
        this.parentNode = parentNode;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public MetaTreeNode<T> getParentNode() {
        return parentNode;
    }

    public void setParentNode(MetaTreeNode<T> parentNode) {
        this.parentNode = parentNode;
    }
}
