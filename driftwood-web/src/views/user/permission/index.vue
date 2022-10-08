<template>
	<div>
		<h1>权限管理</h1>
		<n-tree
			block-line
			cascade
			checkable
			draggable
			:data="permissionStore.permissionData"
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
		<n-modal v-model:show="showPermissionGroupModal">
			<n-card
				style="width: 600px"
				title="编辑权限分组"
				:bordered="false"
				size="huge"
				role="dialog"
				aria-modal="true"
			>
				<n-form
					:model="permissionGroupFormModelRef"
					label-placement="left"
					:label-width="80"
				>
					<n-form-item label="名称">
						<n-input
							v-model:value="permissionGroupFormModelRef.name"
						/>
					</n-form-item>
					<n-form-item label="描述">
						<n-input
							v-model:value="
								permissionGroupFormModelRef.description
							"
							type="textarea"
						/>
					</n-form-item>
				</n-form>
				<template #footer>
					<n-space justify="end">
						<n-button
							type="success"
							@click="handleSaveOrUpdatePermissionGroup"
						>
							确定</n-button
						>
						<n-button
							type="warning"
							@click="hidePermissionGroupModal"
						>
							取消</n-button
						>
					</n-space>
				</template>
			</n-card>
		</n-modal>
		<n-modal v-model:show="showPermissionModal">
			<n-card
				style="width: 600px"
				title="编辑权限"
				:bordered="false"
				size="huge"
				role="dialog"
				aria-modal="true"
			>
				<n-form
					:model="permissionFormModelRef"
					label-placement="left"
					:label-width="80"
				>
					<n-form-item label="名称">
						<n-input v-model:value="permissionFormModelRef.name" />
					</n-form-item>
					<n-form-item label="权限">
						<n-input
							v-model:value="permissionFormModelRef.authority"
						/>
					</n-form-item>
					<n-form-item label="权限类型">
						<n-select
							v-model:value="permissionFormModelRef.type"
							:options="permissionTypeDict"
						/>
					</n-form-item>
					<n-form-item label="描述">
						<n-input
							v-model:value="permissionFormModelRef.description"
							type="textarea"
						/>
					</n-form-item>
				</n-form>
				<template #footer>
					<n-space justify="end">
						<n-button
							type="success"
							@click="handleSaveOrUpdatePermission"
						>
							确定</n-button
						>
						<n-button type="warning" @click="hidePermissionModal">
							取消</n-button
						>
					</n-space>
				</template>
			</n-card>
		</n-modal>
	</div>
</template>

<script setup lang="ts">
import {
	DropdownOption,
	NButton,
	TreeDropInfo,
	TreeOption,
	useMessage,
} from "naive-ui";
import { onMounted, ref } from "vue";
import { usePermissionStore } from "@/store/pinia/user/permission";
import {
	defaultPermissionFormModel,
	defaultPermissionGroupFormModel,
	permissionFormModel,
	permissionGroupFormModel,
} from "@/views/user/permission/data";
import { useObject } from "@/hooks/use-object";

const message = useMessage();
const permissionStore = usePermissionStore();
const { getPropertyValue } = useObject();

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

const handleDragStart = (data: { node: TreeOption; event: DragEvent }) => {
	// 暂无法通过DragEvent相关api来决定是否接受此次拖拽
};

const handleDragEnter = (data: { node: TreeOption; event: DragEvent }) => {
	// 暂无法通过DragEvent相关api来决定是否接受此次拖拽
};

// 由于权限数据tree实际由权限分组数据和权限数据两部分组成，所以有以下情况：
// 一、权限分组之间（权限分组之间不涉及权限数据的权限分组关联变更）：只更新权限分组之间的上下级关系或同级之间的排序关系
// 二、权限之间：
//     1、不变换分组：同组权限数据重新排序
//     2、变换分组：改变被拖拽权限的权限分组数据关联,目标分组权限数据重新排序，原分组权限数据不需要重新排序
// 三、权限 -> 权限分组：与二(2)大致相同，不过默认将被拖拽节点放置在目标分组的最后一位
// 四、权限分组 -> 权限：不允许此种拖拽
const handleDrop = ({ node, dragNode, dropPosition }: TreeDropInfo) => {
	console.log("目标位置的节点：", JSON.stringify(node));
	console.log("被拖拽的节点：", JSON.stringify(dragNode));
	console.log(
		"被拖拽的节点位于目标位置节点的位置",
		JSON.stringify(dropPosition)
	);
	if (dragNode.isGroup) {
		// 被拖拽的节点是权限分组
		if (!node.isGroup) {
			// 目标节点不是分组
			message.warning("不能将[权限分组]节点挂载到[权限]节点下");
			return;
		}
	}
	const [dragNodeSiblings, dragNodeIndex] = findSiblingsAndIndex(
		dragNode,
		permissionStore.permissionData
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
			permissionStore.permissionData
		);
		if (nodeSiblings === null || nodeIndex === null) {
			return;
		}
		nodeSiblings.splice(nodeIndex, 0, dragNode);
	} else if (dropPosition === "after") {
		const [nodeSiblings, nodeIndex] = findSiblingsAndIndex(
			node,
			permissionStore.permissionData
		);
		if (nodeSiblings === null || nodeIndex === null) {
			return;
		}
		nodeSiblings.splice(nodeIndex + 1, 0, dragNode);
	}
	permissionStore.permissionData = Array.from(permissionStore.permissionData);
	console.log("tree", JSON.stringify(permissionStore.permissionData));
};

// 树节点右键菜单
const showDropdown = ref(false);
const handleSelect = () => (showDropdown.value = false);
const handleClickOutSide = () => (showDropdown.value = false);
// 右键菜单选项
const options = ref<DropdownOption[]>([]);
const contextMenuX = ref(0);
const contextMenuY = ref(0);
// 树节点的HTML属性，用来响应左键点击和右键菜单
const nodeProps = ({ option }: { option: TreeOption }) => {
	return {
		onClick() {
			message.info("[Click] " + option.label);
		},
		onContextmenu(e: MouseEvent): void {
			console.log("当前选中节点", JSON.stringify(option));
			if (option.isGroup) {
				options.value = [
					{
						label: "新增权限分组",
						key: "add->" + option.key,
						props: {
							onClick: () => {
								showPermissionGroupModal.value = true;
								permissionGroupModalEditModeIsUpdate.value =
									false;
								Object.assign(
									permissionGroupFormModel,
									defaultPermissionGroupFormModel
								);
							},
						},
					},
					{
						label: "编辑权限分组",
						key: "edit->" + option.key,
						props: {
							onClick: () => {
								showPermissionGroupModal.value = true;
								permissionGroupModalEditModeIsUpdate.value =
									true;
								let cachePermissionGroup =
									permissionStore.getCachePermissionGroupById(
										Number(
											(option.key as string).substring(1)
										)
									);
								if (cachePermissionGroup) {
									Object.keys(
										permissionGroupFormModel
									).forEach((key) => {
										permissionGroupFormModel[
											key as keyof typeof permissionGroupFormModel
										] = getPropertyValue(
											cachePermissionGroup,
											key as keyof typeof cachePermissionGroup
										);
									});
								}
							},
						},
					},
				];
			} else {
				options.value = [
					{
						label: "新增权限",
						key: "add->" + option.key,
						props: {
							onClick: () => {
								showPermissionModal.value = true;
								permissionModalEditModeIsUpdate.value = false;
								Object.assign(
									permissionFormModel,
									defaultPermissionFormModel
								);
							},
						},
					},
					{
						label: "编辑权限",
						key: "edit->" + option.key,
						props: {
							onClick: () => {
								showPermissionModal.value = true;
								permissionModalEditModeIsUpdate.value = true;
								let cachePermission =
									permissionStore.getCachePermissionById(
										option.key as number
									);
								if (cachePermission) {
									Object.keys(permissionFormModel).forEach(
										(key) => {
											permissionFormModel[
												key as keyof typeof permissionFormModel
											] = getPropertyValue(
												cachePermission,
												key as keyof typeof cachePermission
											);
										}
									);
								}
							},
						},
					},
				];
			}
			showDropdown.value = true;
			contextMenuX.value = e.clientX;
			contextMenuY.value = e.clientY;
			e.preventDefault();
		},
	};
};
// 是不是编辑模式
const permissionGroupModalEditModeIsUpdate = ref(true);
// 是否展示权限分组编辑模态框
const showPermissionGroupModal = ref(false);
// 权限分组数据model
const permissionGroupFormModelRef = ref(permissionGroupFormModel);
// 是不是编辑模式
const permissionModalEditModeIsUpdate = ref(true);
// 是否展示权限编辑模态框
const showPermissionModal = ref(false);
// 权限数据model
const permissionFormModelRef = ref(permissionFormModel);
// 权限类型
const permissionTypeDict = [
	{
		label: "功能服务",
		value: 1,
	},
	{
		label: "界面元素",
		value: 2,
	},
];

const handleSaveOrUpdatePermissionGroup = async () => {
	console.log(JSON.stringify(permissionGroupFormModelRef.value));
};
const hidePermissionGroupModal = () => {
	showPermissionGroupModal.value = false;
};

const handleSaveOrUpdatePermission = async () => {
	console.log(JSON.stringify(permissionFormModelRef.value));
};
const hidePermissionModal = () => {
	showPermissionModal.value = false;
};

const refreshPermission = async () => {
	let [permissionGroupList, permissionList] = await Promise.all([
		permissionStore.listPermissionGroup(),
		permissionStore.listPermission(),
	]);
	if (permissionGroupList && permissionList) {
		permissionStore.initCachePermissionGroup(permissionGroupList);
		permissionStore.initCachePermission(permissionList);
		await permissionStore.refreshPermission(
			permissionGroupList,
			permissionList
		);
	}
};

const initialize = async () => {
	await refreshPermission();
};
onMounted(initialize);
</script>

<style lang="scss" scoped></style>
