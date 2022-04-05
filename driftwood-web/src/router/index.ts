import {createRouter, createWebHistory} from "vue-router";

import Login from "../views/Login.vue";
import Home from "../views/Home.vue";

// admin
import Oauth2RegisteredClient from "../views/admin/Oauth2RegisteredClient.vue";

// user
import User from "../views/user/User.vue";
import Role from "../views/user/Role.vue";
import Permission from "../views/user/Permission.vue";
import Menu from "../views/user/Menu.vue";

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
			meta: {
				requiresAuth: true
			},
			children: [
				{
					path: "client",
					name: "Oauth2RegisteredClient",
					component: Oauth2RegisteredClient
				},
				{
					path: "user",
					name: "User",
					component: User
				},
				{
					path: "role",
					name: "Role",
					component: Role
				},
				{
					path: "permission",
					name: "Permission",
					component: Permission
				},
				{
					path: "menu",
					name: "Menu",
					component: Menu
				},
			]
		}
	],
});

router.beforeEach((to, from, next) => {
	if (to.meta.requiresAuth && localStorage.getItem("accessToken") === "") {
		console.log("Route------------->");
		console.log(to.fullPath);
		console.log("Route------------->");
		if (typeof (to.query.redirect) === "undefined") {
			next({
				name: "Login",
				query: {redirect: to.fullPath}
			});
		} else {
			next({
				name: "Login",
				query: {redirect: to.fullPath}
			});
		}
	} else {
		next();
	}
});

export default router;
