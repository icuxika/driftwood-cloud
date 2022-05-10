import { createRouter, createWebHistory } from "vue-router";

import Login from "@/views/login.vue";
import Home from "@/views/home.vue";

// index
import Index from "@/views/index/index.vue";

// admin
import Oauth2RegisteredClient from "@/views/admin/oauth2-registered-client.vue";

// user
import User from "@/views/user/user.vue";
import Role from "@/views/user/role.vue";
import Permission from "@/views/user/permission.vue";
import Menu from "@/views/user/menu.vue";

declare module "vue-router" {
	interface RouteMeta {
		/**
		 * 需要登录
		 */

		requiresAuth?: boolean;

		/**
		 * 标题
		 */
		title?: string;

		/**
		 * 是否固定
		 */
		fixed?: boolean;
	}
}

const router = createRouter({
	history: createWebHistory(),
	routes: [
		{
			path: "/login",
			name: "Login",
			component: Login,
		},
		{
			path: "/",
			name: "Home",
			component: Home,
			meta: {},
			children: [
				{
					path: "",
					redirect: "/index",
				},
				{
					path: "index",
					name: "Index",
					component: Index,
					meta: {
						requiresAuth: true,
						title: "首页",
						fixed: true,
					},
				},
				{
					path: "client",
					name: "Oauth2RegisteredClient",
					component: Oauth2RegisteredClient,
					meta: {
						title: "客户端",
						fixed: false,
					},
				},
				{
					path: "user",
					name: "User",
					component: User,
					meta: {
						title: "用户",
						fixed: false,
					},
				},
				{
					path: "role",
					name: "Role",
					component: Role,
					meta: {
						title: "角色",
						fixed: false,
					},
				},
				{
					path: "permission",
					name: "Permission",
					component: Permission,
					meta: {
						title: "权限",
						fixed: false,
					},
				},
				{
					path: "menu",
					name: "Menu",
					component: Menu,
					meta: {
						title: "菜单",
						fixed: false,
					},
				},
			],
		},
	],
});

router.beforeEach((to, from, next) => {
	if (to.meta.requiresAuth && localStorage.getItem("accessToken") === "") {
		console.log("Route------------->");
		console.log(to.fullPath);
		console.log("Route------------->");
		if (typeof to.query.redirect === "undefined") {
			next({
				name: "Login",
				query: { redirect: to.fullPath },
			});
		} else {
			next({
				name: "Login",
				query: { redirect: to.fullPath },
			});
		}
	} else {
		next();
	}
});

export default router;
