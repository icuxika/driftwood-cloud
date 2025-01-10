import { createRouter, createWebHistory } from "vue-router";

import eventEmitter from "@/events/event-emitter";
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

eventEmitter.on("API:NOT_LOGGED_IN", () => {
    console.log("登录失效");
    window.$message.warning("登录失效，请重新登录");
    localStorage.setItem("accessToken", "");
    // 跳转登录页面
    const currentPath = router.currentRoute.value.fullPath;
    let newDirect = currentPath;
    // 移除被跳转的路径携带的query参数，防止重复
    const existQueryIndex = currentPath.indexOf("?");
    if (existQueryIndex != -1) {
        newDirect = currentPath.slice(0, existQueryIndex);
    }
    if (currentPath.indexOf("/login") == -1) {
        router.push({
            path: "/login",
            replace: true,
            query: {
                redirect: newDirect,
            },
        });
    }
});

export default router;
