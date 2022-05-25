package com.icuxika.util;

import com.icuxika.constant.SystemConstant;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 构建树形结构工具类
 *
 * @author icuxika
 */
public class TreeUtil {

    /**
     * 构建树形结构
     *
     * @param list           数据源
     * @param idGetter       id获取器
     * @param parentIdGetter 父节点id获取器
     * @param <T>            数据类型
     * @return 树形结构
     */
    public static <T> List<TreeNode<T>> buildTree(List<T> list, Function<T, Long> idGetter, Function<T, Long> parentIdGetter) {
        return buildTree(list, SystemConstant.TREE_ROOT_ID, idGetter, parentIdGetter, null);
    }

    /**
     * 构建树形结构
     *
     * @param list           数据源
     * @param rootId         根节点id
     * @param idGetter       id获取器
     * @param parentIdGetter 父节点id获取器
     * @param <T>            数据类型
     * @return 树形结构
     */
    public static <T> List<TreeNode<T>> buildTree(List<T> list, Long rootId, Function<T, Long> idGetter, Function<T, Long> parentIdGetter) {
        return buildTree(list, rootId, idGetter, parentIdGetter, null);
    }

    /**
     * 构建树形结构
     *
     * @param list           数据源
     * @param idGetter       id获取器
     * @param parentIdGetter 父节点id获取器
     * @param comparator     排序器
     * @param <T>            数据类型
     * @return 树形结构
     */
    public static <T> List<TreeNode<T>> buildTree(List<T> list, Function<T, Long> idGetter, Function<T, Long> parentIdGetter, Comparator<T> comparator) {
        return buildTree(list, SystemConstant.TREE_ROOT_ID, idGetter, parentIdGetter, comparator);
    }

    /**
     * 构建树形结构
     *
     * @param list           数据源
     * @param rootId         根节点id
     * @param idGetter       id获取器
     * @param parentIdGetter 父节点id获取器
     * @param comparator     排序器
     * @param <T>            数据类型
     * @return 树形结构
     */
    public static <T> List<TreeNode<T>> buildTree(List<T> list, Long rootId, Function<T, Long> idGetter, Function<T, Long> parentIdGetter, Comparator<T> comparator) {
        // 排序
        Optional.ofNullable(comparator).ifPresent(list::sort);

        List<TreeNode<T>> result = new ArrayList<>();

        Map<Long, TreeNode<T>> map = list.stream().collect(Collectors.toMap(idGetter, t -> {
            TreeNode<T> treeNode = new TreeNode<>();
            treeNode.setId(idGetter.apply(t));
            treeNode.setParentId(parentIdGetter.apply(t));
            treeNode.setChildren(new ArrayList<>());
            treeNode.setData(t);
            return treeNode;
        }, (x, y) -> y));

        // 不对id和parentId进行判空
        list.forEach(item -> {
            if (rootId.equals(parentIdGetter.apply(item))) {
                result.add(map.get(idGetter.apply(item)));
            } else {
                Optional.ofNullable(map.get(parentIdGetter.apply(item))).ifPresent(parent -> parent.getChildren().add(map.get(idGetter.apply(item))));
            }
        });

        return result;
    }

    /**
     * 展平树结构（不适合性能要求高的场景）
     *
     * @param tree 使用 {@link TreeUtil#buildTree(List, Function, Function)} 构建的树结构数据
     * @param <T>  树节点携带的数据
     * @return 返回的Stream包含所有的树节点在同一级别
     */
    public static <T> Stream<TreeNode<T>> flattenTree(List<TreeNode<T>> tree) {
        return flattenTree(tree, t -> true);
    }

    public static <T> Stream<TreeNode<T>> flattenTree(List<TreeNode<T>> tree, Predicate<T> predicate) {
        return tree.stream().flatMap(treeNode -> treeNode.flatten(predicate));
    }
}
