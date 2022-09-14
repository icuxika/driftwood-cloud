<template>
	<div>
		<h1>菜单管理</h1>
		<n-tree
			block-line
			cascade
			checkable
			draggable
			:data="menuStore.menuData"
			:node-props="nodeProps"
			@update:checked-keys="updateCheckedKeys"
			@drop="handleDrop"
			@dragstart="handleDragStart"
			@dragenter="handleDragEnter"
		/>
		<n-dropdown
			trigger="manual"
			placement="bottom-start"
			:show="showDropdown"
			:options="options"
			:x="contextMenuX"
			:y="contextMenuY"
			@select="handleSelect"
			@clickoutside="handleClickOutSide"
		/>
		<n-modal v-model:show="showMenuEditModal">
			<n-card
				style="width: 600px"
				title="编辑权限分组"
				:bordered="false"
				size="huge"
				role="dialog"
				aria-modal="true"
			>
				<n-form
					:model="menuEditFormModel"
					label-placement="left"
					:label-width="80"
				>
					<n-form-item label="菜单类型">
						<n-select
							v-model:value="menuEditFormModel.type"
							:options="menuTypeDict"
						/>
					</n-form-item>
					<n-form-item label="名称">
						<n-input v-model:value="menuEditFormModel.name" />
					</n-form-item>
					<n-form-item label="图标">
						<n-input v-model:value="menuEditFormModel.icon" />
					</n-form-item>
					<n-form-item label="路径">
						<n-input v-model:value="menuEditFormModel.path" />
					</n-form-item>
				</n-form>
				<template #footer>
					<n-space justify="end">
						<n-button type="success" @click="handleUpdateMenu">
							确定</n-button
						>
						<n-button type="warning" @click="hideMenuEditModal">
							取消</n-button
						>
					</n-space>
				</template>
			</n-card>
		</n-modal>
	</div>
</template>

<script setup lang="ts">
import { useMenuStore } from "@/store/pinia/user/menu";
import { DropdownOption, TreeDropInfo, TreeOption, useMessage } from "naive-ui";
import { onMounted, ref } from "vue";

const message = useMessage();
const menuStore = useMenuStore();

const updateCheckedKeys = (checkedKeys: string[]) => {
	console.log(checkedKeys);
};

// 查找节点在tree中的与其同级的所有节点和其在这些节点中的索引
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

const handleDragStart = (data: { node: TreeOption; event: DragEvent }) => {};
const handleDragEnter = (data: { node: TreeOption; event: DragEvent }) => {};
const handleDrop = ({ node, dragNode, dropPosition }: TreeDropInfo) => {
	console.log("目标位置的节点：", JSON.stringify(node));
	console.log("被拖拽的节点：", JSON.stringify(dragNode));
	console.log(
		"被拖拽的节点位于目标位置节点的位置",
		JSON.stringify(dropPosition)
	);
	if ((dragNode.type as number) < (node.type as number)) {
		message.warning("不能高级别节点挂载到低级别节点下");
		return;
	}
	const [dragNodeSiblings, dragNodeIndex] = findSiblingsAndIndex(
		dragNode,
		menuStore.menuData
	);
	if (dragNodeSiblings === null || dragNodeIndex === null) {
		message.warning("当前节点数据异常，无法完成操作");
		return;
	}
	console.log("被拖拽节点同级的所有节点: ", JSON.stringify(dragNodeSiblings));
	console.log("被拖拽节点在同级所有节点的索引: ", dragNodeIndex);
	// 目标节点是分组
	dragNodeSiblings.splice(dragNodeIndex, 1);
	if (dropPosition === "inside") {
		if (node.children) {
			node.children.unshift(dragNode);
		} else {
			node.children = [dragNode];
		}
	} else if (dropPosition === "before") {
		const [nodeSiblings, nodeIndex] = findSiblingsAndIndex(
			node,
			menuStore.menuData
		);
		if (nodeSiblings === null || nodeIndex === null) {
			return;
		}
		nodeSiblings.splice(nodeIndex, 0, dragNode);
	} else if (dropPosition === "after") {
		const [nodeSiblings, nodeIndex] = findSiblingsAndIndex(
			node,
			menuStore.menuData
		);
		if (nodeSiblings === null || nodeIndex === null) {
			return;
		}
		nodeSiblings.splice(nodeIndex + 1, 0, dragNode);
	}
	menuStore.menuData = Array.from(menuStore.menuData);
	console.log("tree", JSON.stringify(menuStore.menuData));
};

// 树节点右键菜单
const showDropdown = ref(false);
const handleSelect = () => (showDropdown.value = false);
const handleClickOutSide = () => (showDropdown.value = false);
// 右键菜单选项
const options = ref<DropdownOption[]>([]);
const contextMenuX = ref(0);
const contextMenuY = ref(0);
const nodeProps = ({ option }: { option: TreeOption }) => {
	return {
		onContextmenu(e: MouseEvent): void {
			options.value = [
				{
					label: "编辑",
					key: "edit->" + option.key,
					props: {
						onClick: () => {
							showMenuEditModal.value = true;
							let cacheMenu = menuStore.getCacheMenuById(
								option.key as number
							);
							if (cacheMenu) {
								Object.assign(
									menuEditFormModel.value,
									cacheMenu
								);
							}
						},
					},
				},
			];
			showDropdown.value = true;
			contextMenuX.value = e.clientX;
			contextMenuY.value = e.clientY;
			e.preventDefault();
		},
	};
};

// 是否展示菜单编辑模态框
const showMenuEditModal = ref(false);
const defaultMenuEditFormModel = {
	id: 0,
	type: 0,
	name: "",
	icon: "",
	path: "",
};
const menuEditFormModel = ref(defaultMenuEditFormModel);
// 菜单类型
const menuTypeDict = [
	{
		label: "菜单-应用",
		value: 1,
	},
	{
		label: "菜单-服务",
		value: 2,
	},
	{
		label: "菜单-URL",
		value: 3,
	},
	{
		label: "按钮",
		value: 4,
	},
];

const handleUpdateMenu = async () => {
	console.log(JSON.stringify(menuEditFormModel.value));
};
const hideMenuEditModal = () => {
	showMenuEditModal.value = false;
	menuEditFormModel.value = defaultMenuEditFormModel;
};

const refreshMenu = async () => {
	let menuList = await menuStore.listMenu();
	if (menuList) {
		menuStore.cacheMenuList = menuList;
		await menuStore.refreshMenu(menuList);
	}
};

const initialize = async () => {
	await refreshMenu();
};
onMounted(initialize);
</script>

<style lang="scss" scoped></style>
