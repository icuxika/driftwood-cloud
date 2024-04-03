import { RouteRecordRaw } from "vue-router";

export const userRoutes: RouteRecordRaw[] = [
    {
        path: "user/user",
        name: "User",
        component: () => import("@/views/user/user/index.vue"),
        meta: {
            title: "用户",
            fixed: false,
        },
    },
    {
        path: "user/role",
        name: "Role",
        component: () => import("@/views/user/role/index.vue"),
        meta: {
            title: "角色",
            fixed: false,
        },
    },
    {
        path: "user/permission",
        name: "Permission",
        component: () => import("@/views/user/permission/index.vue"),
        meta: {
            title: "权限",
            fixed: false,
        },
    },
    {
        path: "user/menu",
        name: "Menu",
        component: () => import("@/views/user/menu/index.vue"),
        meta: {
            title: "菜单",
            fixed: false,
        },
    },
];
