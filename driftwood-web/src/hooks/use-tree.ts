import { TreeOption } from "naive-ui";

export const useTree = () => {
	/**
	 * 查找节点在tree中的与其同级的所有节点和其在这些节点中的索引
	 * @param node
	 * @param nodes
	 */
	const findSiblingsAndIndex = (
		node: TreeOption,
		nodes?: TreeOption[]
	): [TreeOption[], number] | [null, null] => {
		if (!nodes) return [null, null];
		for (let i = 0; i < nodes.length; ++i) {
			const siblingNode = nodes[i];
			if (siblingNode.key === node.key) return [nodes, i];
			const [siblings, index] = findSiblingsAndIndex(
				node,
				siblingNode.children
			);
			if (siblings && index !== null) return [siblings, index];
		}
		return [null, null];
	};

	const recursiveSearchOptionByKey = (
		key: number,
		tree: TreeOption[],
		result: TreeOption[]
	) => {
		for (let i = 0; i < tree.length; i++) {
			if (tree[i].key === key) {
				result.push(tree[i]);
				break;
			}
			const children = tree[i].children;
			if (children) {
				recursiveSearchOptionByKey(key, children, result);
			}
		}
	};

	const getParentTreeOption = (
		parentKey: number,
		tree: TreeOption[]
	): TreeOption | TreeOption[] | undefined => {
		if (parentKey === 0) {
			return tree;
		}
		const resultWrapper: TreeOption[] = [];
		recursiveSearchOptionByKey(parentKey, tree, resultWrapper);
		if (resultWrapper.length == 1) {
			return resultWrapper[0];
		}
		return undefined;
	};

	return {
		findSiblingsAndIndex,
		getParentTreeOption,
	};
};
