<template>
	<n-layout-sider
		bordered
		collapse-mode="width"
		:collapsed-width="64"
		:width="240"
		:collapsed="collapsed"
		show-trigger
		@collapse="collapsed = true"
		@expand="collapsed = false"
	>
		<n-button @click="test">加载菜单</n-button>
		<n-menu
			ref="menuRef"
			:collapsed="collapsed"
			:collapsed-width="64"
			:collapsed-icon-size="22"
			:options="sideMenuStore.menuOptions"
			key-field="key"
			label-field="label"
			children-field="children"
		/>
	</n-layout-sider>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useSideMenuStore } from "@/store/pinia/side-menu";
import { MenuInst } from "naive-ui";

const sideMenuStore = useSideMenuStore();

const menuRef = ref<MenuInst | null>(null);
const collapsed = ref(false);

const test = async () => {
	const menuList = [
		{
			id: 2,
			createTime: "2022-08-31 13:59:37",
			createUserId: 2,
			updateTime: "2022-08-31 13:59:37",
			updateUserId: 2,
			parentId: 0,
			type: 1,
			name: "系统",
			icon: "BookOutline",
			path: "/",
			sequence: 0,
			status: 0,
		},
		{
			id: 3,
			createTime: "2022-09-01 12:13:38",
			createUserId: 2,
			updateTime: "2022-09-01 12:13:38",
			updateUserId: 2,
			parentId: 2,
			type: 2,
			name: "授权服务",
			icon: "BookOutline",
			path: "/",
			sequence: 0,
			status: 0,
		},
		{
			id: 4,
			createTime: "2022-09-01 12:14:02",
			createUserId: 2,
			updateTime: "2022-09-01 12:14:02",
			updateUserId: 2,
			parentId: 2,
			type: 2,
			name: "用户服务",
			icon: "BookOutline",
			path: "/",
			sequence: 0,
			status: 0,
		},
		{
			id: 5,
			createTime: "2022-09-01 12:14:48",
			createUserId: 2,
			updateTime: "2022-09-01 12:14:48",
			updateUserId: 2,
			parentId: 3,
			type: 3,
			name: "客户端管理",
			icon: "BookOutline",
			path: "/admin/client",
			sequence: 0,
			status: 0,
		},
		{
			id: 6,
			createTime: "2022-09-01 12:15:50",
			createUserId: 2,
			updateTime: "2022-09-01 12:15:50",
			updateUserId: 2,
			parentId: 4,
			type: 3,
			name: "用户管理",
			icon: "BookOutline",
			path: "/user/user",
			sequence: 0,
			status: 0,
		},
		{
			id: 7,
			createTime: "2022-09-01 12:16:24",
			createUserId: 2,
			updateTime: "2022-09-01 12:16:24",
			updateUserId: 2,
			parentId: 4,
			type: 3,
			name: "角色管理",
			icon: "BookOutline",
			path: "/user/role",
			sequence: 0,
			status: 0,
		},
		{
			id: 8,
			createTime: "2022-09-01 12:16:39",
			createUserId: 2,
			updateTime: "2022-09-01 12:16:39",
			updateUserId: 2,
			parentId: 4,
			type: 3,
			name: "权限管理",
			icon: "BookOutline",
			path: "/user/permission",
			sequence: 0,
			status: 0,
		},
		{
			id: 9,
			createTime: "2022-09-01 12:17:06",
			createUserId: 2,
			updateTime: "2022-09-01 12:17:06",
			updateUserId: 2,
			parentId: 4,
			type: 3,
			name: "菜单管理",
			icon: "BookOutline",
			path: "/user/menu",
			sequence: 0,
			status: 0,
		},
	];
	await sideMenuStore.refreshMenu(menuList);
};
</script>

<style lang="scss" scoped></style>
