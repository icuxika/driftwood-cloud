import { createRouter, createWebHistory } from "vue-router";

import { basicRoutes } from "@/router/routes";

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
    routes: basicRoutes,
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
