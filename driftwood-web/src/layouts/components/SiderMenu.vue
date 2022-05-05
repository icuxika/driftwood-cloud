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
		<n-button @click="test">测试</n-button>
		<n-menu
			:collapsed="collapsed"
			:default-expand-all="true"
			:collapsed-width="64"
			:collapsed-icon-size="22"
			:options="menuOptions"
			key-field="key"
			label-field="label"
			children-field="children"
			@update:value="handleUpdateValue"
		/>
	</n-layout-sider>
</template>

<script setup lang="ts">
import { Component, h, ref } from "vue";
import type { MenuOption } from "naive-ui";
import { NIcon } from "naive-ui";
import { BookOutline as BookIcon } from "@vicons/ionicons5";
import { RouterLink } from "vue-router";

const renderIcon = (icon: Component) => {
	return () => h(NIcon, null, { default: () => h(icon) });
};

const collapsed = ref(false);
const menuValue = ref("");

const menuOptions: MenuOption[] = [
	{
		label: "系统",
		key: "system",
		icon: renderIcon(BookIcon),
		children: [
			{
				type: "group",
				label: "授权服务",
				key: "admin-service",
				children: [
					{
						label: () =>
							h(
								RouterLink,
								{
									to: {
										name: "Oauth2RegisteredClient",
									},
								},
								{ default: () => "客户端管理" }
							),
						key: "client",
						icon: renderIcon(BookIcon),
					},
				],
			},
			{
				type: "group",
				label: "用户服务",
				key: "user-service",
				children: [
					{
						label: () =>
							h(
								RouterLink,
								{
									to: {
										name: "User",
									},
								},
								{ default: () => "用户管理" }
							),
						key: "user",
						icon: renderIcon(BookIcon),
					},
					{
						label: () =>
							h(
								RouterLink,
								{
									to: {
										name: "Role",
									},
								},
								{ default: () => "角色管理" }
							),
						key: "role",
						icon: renderIcon(BookIcon),
					},
					{
						label: () =>
							h(
								RouterLink,
								{
									to: {
										name: "Permission",
									},
								},
								{ default: () => "权限管理" }
							),
						key: "permission",
						icon: renderIcon(BookIcon),
					},
					{
						label: () =>
							h(
								RouterLink,
								{
									to: {
										name: "Menu",
									},
								},
								{ default: () => "菜单管理" }
							),
						key: "menu",
						icon: renderIcon(BookIcon),
					},
				],
			},
		],
	},
];

const test = () => {
	console.log(menuValue.value);
};

const handleUpdateValue = (key: string, item: MenuOption) => {
	// console.log(JSON.stringify(key));
	// console.log(JSON.stringify(item));
};
</script>

<style lang="scss" scoped></style>
