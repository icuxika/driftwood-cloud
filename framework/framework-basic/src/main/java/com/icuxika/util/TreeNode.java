package com.icuxika.util;

import java.util.List;

/**
 * 树节点
 *
 * @param <T> 数据类型
 * @author icuxika
 */
public class TreeNode<T> {

    /**
     * 数据id
     */
    private Long id;

    /**
     * 数据父id
     */
    private Long parentId;

    /**
     * 数据
     */
    private T data;

    /**
     * 子节点
     */
    private List<TreeNode<T>> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children;
    }
}
