package com.icuxika.framework.basic.util;

import com.icuxika.framework.basic.constant.SystemConstant;

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

    /**
     * 使用Stream来对树进行深度优先遍历并能够提供树节点的深度信息，由 <a href="https://zhuanlan.zhihu.com/p/82508008">从树的深度优先遍历谈谈Stream.iterate的用法</a>
     * 文中所提供代码改编而来，此函数保证了完全惰性（只计算必要的节点及其子节点）
     *
     * @param treeNodeList 使用 {@link TreeUtil#buildTree(List, Function, Function)} 构建的树结构数据
     * @param predicate    过滤条件
     * @param <T>          树节点携带的数据
     * @return 返回的Stream包含所有的树节点在同一级别
     */
    public static <T> Stream<MetaTreeNode<T>> lazyDFSSearch(List<TreeNode<T>> treeNodeList, Predicate<MetaTreeNode<T>> predicate) {
        Stream<MetaTreeNode<T>> firstDepthStream = treeNode2Meta(treeNodeList, 1, null).filter(predicate);
        MetaTreeNodeStackHead<T> rootStack = new MetaTreeNodeStackHead<>(new MetaTreeNode<>(0, null), firstDepthStream.iterator(), firstDepthStream, null);
        return Stream.iterate(
                rootStack,
                Objects::nonNull,
                stack -> {
                    if (stack.getNode().getDepth() == 0) {
                        Iterator<MetaTreeNode<T>> iterator = stack.getSiblingIterator();
                        if (iterator.hasNext()) {
                            return new MetaTreeNodeStackHead<>(iterator.next(), iterator, stack.getStream(), stack);
                        }
                        return null;
                    } else {
                        final Stream<MetaTreeNode<T>> children = treeNode2Meta(stack.getNode().getChildren(), stack.node.getDepth() + 1, stack.node);
                        if (children != null) {
                            final Iterator<MetaTreeNode<T>> iterator = children.filter(predicate).iterator();
                            if (iterator.hasNext()) {
                                return new MetaTreeNodeStackHead<>(iterator.next(), iterator, children, stack);
                            }
                        }
                        final Optional<MetaTreeNodeStackHead<T>> nextHead = Stream.iterate(
                                        stack,
                                        Objects::nonNull,
                                        head -> {
                                            if (head.getSiblingIterator().hasNext()) {
                                                return head;
                                            } else {
                                                head.getStream().close();
                                                return head.getPreviousHead();
                                            }
                                        }
                                )
                                .dropWhile(s -> !s.getSiblingIterator().hasNext())
                                .findFirst();
                        return nextHead
                                .map(s -> new MetaTreeNodeStackHead<T>(s.getSiblingIterator().next(), s.getSiblingIterator(), s.getStream(), s.getPreviousHead()))
                                .orElse(null);
                    }
                }
        ).filter(p -> p.getNode().getDepth() > 0).map(MetaTreeNodeStackHead::getNode);
    }

    private static <T> Stream<MetaTreeNode<T>> treeNode2Meta(List<TreeNode<T>> treeNodeList, int depth, MetaTreeNode<T> parentNode) {
        if (treeNodeList == null) {
            return null;
        }
        return treeNodeList.stream().map(treeNode -> {
            MetaTreeNode<T> metaTreeNode = new MetaTreeNode<>(depth, parentNode);
            metaTreeNode.setId(treeNode.getId());
            metaTreeNode.setParentId(treeNode.getParentId());
            metaTreeNode.setData(treeNode.getData());
            metaTreeNode.setChildren(treeNode.getChildren());
            return metaTreeNode;
        });
    }

    private static class MetaTreeNodeStackHead<T> {

        private MetaTreeNode<T> node;

        /**
         * 包含当前节点的集合迭代器
         */
        private Iterator<MetaTreeNode<T>> siblingIterator;

        private Stream<MetaTreeNode<T>> stream;

        private MetaTreeNodeStackHead<T> previousHead;

        public MetaTreeNodeStackHead(MetaTreeNode<T> node, Iterator<MetaTreeNode<T>> siblingIterator, Stream<MetaTreeNode<T>> stream, MetaTreeNodeStackHead<T> previousHead) {
            this.node = node;
            this.siblingIterator = siblingIterator;
            this.stream = stream;
            this.previousHead = previousHead;
        }

        public MetaTreeNode<T> getNode() {
            return node;
        }

        public void setNode(MetaTreeNode<T> node) {
            this.node = node;
        }

        public Iterator<MetaTreeNode<T>> getSiblingIterator() {
            return siblingIterator;
        }

        public void setSiblingIterator(Iterator<MetaTreeNode<T>> siblingIterator) {
            this.siblingIterator = siblingIterator;
        }

        public Stream<MetaTreeNode<T>> getStream() {
            return stream;
        }

        public void setStream(Stream<MetaTreeNode<T>> stream) {
            this.stream = stream;
        }

        public MetaTreeNodeStackHead<T> getPreviousHead() {
            return previousHead;
        }

        public void setPreviousHead(MetaTreeNodeStackHead<T> previousHead) {
            this.previousHead = previousHead;
        }
    }
}
