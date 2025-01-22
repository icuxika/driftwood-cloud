import { RouteRecordRaw } from "vue-router";

export const testRoutes: RouteRecordRaw[] = [
    {
        path: "test/render-table",
        name: "RenderTable",
        component: () => import("@/views/test/render-table/index.vue"),
        meta: {
            title: "表格",
            fixed: false,
        },
    },
    {
        path: "test/logic-flow",
        name: "LogicFlow",
        component: () => import("@/views/test/logic-flow/index.vue"),
        meta: {
            title: "LogicFlow",
            fixed: false,
        },
    },
    {
        path: "test/ai-chat",
        name: "AIChat",
        component: () => import("@/views/test/ai-chat/index.vue"),
        meta: {
            title: "AI对话",
            fixed: false,
        },
    },
    {
        path: "test/audio-player",
        name: "AudioPlayer",
        component: () => import("@/views/test/audio-player/index.vue"),
        meta: {
            title: "音乐播放器",
            fixed: false,
        },
    },
];
