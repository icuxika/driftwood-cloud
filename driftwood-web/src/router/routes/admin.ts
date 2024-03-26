import { RouteRecordRaw } from "vue-router";

export const adminRoutes: RouteRecordRaw[] = [
    {
        path: "admin/client",
        name: "Oauth2RegisteredClient",
        component: () =>
            import("@/views/admin/oauth2-registered-client/index.vue"),
        meta: {
            title: "客户端",
            fixed: false,
        },
    },
];
