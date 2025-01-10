import { RouteRecordRaw } from "vue-router";

export const testRoutes: RouteRecordRaw[] = [
    {
        path: "test/table",
        name: "RenderTable",
        component: () => import("@/views/test/render-table/index.vue"),
        meta: {
            title: "表格",
            fixed: false,
        },
    },
];
