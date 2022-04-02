import {createRouter, createWebHistory} from "vue-router";

import Home from "../views/Home.vue";
import Login from "../views/Login.vue";

const router = createRouter({
	history: createWebHistory(),
	routes: [
		{
			path: "/",
			name: "Home",
			component: Home,
			meta: {
				requiresAuth: true
			}
		},
		{
			path: "/login",
			name: "Login",
			component: Login,
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
