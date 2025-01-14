import { adminRoutes } from "@/router/routes/admin";
import { userRoutes } from "@/router/routes/user";
import { RouteRecordRaw } from "vue-router";
import { testRoutes } from "./test";

const LOGIN_ROUTE: RouteRecordRaw = {
    path: "/login",
    name: "Login",
    component: () => import("@/views/login.vue"),
};

const INDEX_REDIRECT: RouteRecordRaw = {
    path: "",
    redirect: "/index",
};

const INDEX_ROUTE: RouteRecordRaw = {
    path: "index",
    name: "Index",
    component: () => import("@/views/index/index.vue"),
    meta: {
        requiresAuth: true,
        title: "首页",
        fixed: true,
    },
};

export const basicRoutes: RouteRecordRaw[] = [
    LOGIN_ROUTE,
    {
        path: "/",
        component: () => import("@/views/home.vue"),
        children: [
            INDEX_REDIRECT,
            INDEX_ROUTE,
            ...adminRoutes,
            ...userRoutes,
            ...testRoutes,
        ],
    },
    {
        path: "/:pathMatch(.*)*",
        name: "NotFound",
        component: () => import("@/components/NotFound.vue"),
    },
];
