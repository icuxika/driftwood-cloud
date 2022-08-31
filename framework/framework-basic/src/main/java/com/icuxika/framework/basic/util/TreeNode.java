package com.icuxika.framework.basic.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

    /**
     * 展平树结构以便过滤（不适合性能要求高的场景）
     *
     * @return 返回的Stream包含所有的树节点在同一级别
     */
    public Stream<TreeNode<T>> flatten() {
        return flatten(t -> true);
    }

    public Stream<TreeNode<T>> flatten(Predicate<T> predicate) {
        return Stream.concat(
                Stream.of(this),
                childrenStream().filter(treeNode -> predicate.test(treeNode.getData())).flatMap(TreeNode::flatten)
        );
    }

    private Stream<TreeNode<T>> childrenStream() {
        return Optional.ofNullable(children).orElse(Collections.emptyList()).stream();
    }
}
