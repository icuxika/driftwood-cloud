<template>
    <n-el>
        <n-flex>
            <VueDraggable
                ref="el"
                v-model="navRouteList"
                class="draggable-container"
            >
                <div
                    v-for="item in navRouteList"
                    :key="item.path"
                    class="draggable-container-item"
                    :class="{
                        'draggable-container-item-active':
                            item.path === activePath,
                    }"
                    @click.stop="goto(item as any)"
                >
                    <span>{{ item.meta.title }}</span>
                    <n-icon
                        v-if="!item.meta.fixed"
                        size="16"
                        @click.stop="closeTab(item as any)"
                    >
                        <CloseIcon />
                    </n-icon>
                </div>
            </VueDraggable>
        </n-flex>
    </n-el>
</template>

<script setup lang="ts">
import { NavRoute, useNavStore } from "@/store/nav";
import { CloseOutline as CloseIcon } from "@vicons/ionicons5";
import { computed, ref, watch } from "vue";
import { VueDraggable } from "vue-draggable-plus";
import { _RouteLocationBase, useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();

const navStore = useNavStore();

const navRouteList = computed<NavRoute[]>({
    get() {
        return navStore.navRouteList;
    },
    set(value) {
        navStore.initNavRoute(value);
    },
});

const getRouteLocationBase = (
    route: _RouteLocationBase
): _RouteLocationBase => {
    const { path, fullPath, query, hash, name, params, redirectedFrom, meta } =
        route;
    return {
        path,
        fullPath,
        query,
        hash,
        name,
        params,
        redirectedFrom,
        meta,
    };
};

// 当前激活的导航标签
const activePath = ref(route.path);
// 初始化首页标签
let initNavRouteList = router
    .getRoutes()
    .filter((item) => item.path === "/index");
navStore.initNavRoute(initNavRouteList);

const excludedPath: string[] = ["Login"];

// 观察路由变化，从而改变当前激活标签并添加导航标签
watch(
    () => route.path,
    (to) => {
        if (excludedPath.includes(route.name as string)) {
            return;
        }
        activePath.value = to;
        navStore.addNavRoute(getRouteLocationBase(route));
    },
    { immediate: true }
);

// 传递给vue.draggable.next(https://github.com/SortableJS/vue.draggable.next)依赖的Sortable(https://github.com/SortableJS/Sortable)的参数
const dragOptions = {
    group: "description",
    animation: 250,
    disabled: false,
    ghostClass: "ghost",
};

// 导航
const goto = (item: _RouteLocationBase) => {
    if (item.path === activePath.value) return;
    activePath.value = item.path;
    router.push(item);
};

// 关闭标签
const closeTab = (item: _RouteLocationBase) => {
    navStore.removeNavRoute(item.path);
    if (activePath.value === item.path) {
        const newRoute =
            navRouteList.value[Math.max(0, navRouteList.value.length - 1)];
        activePath.value = newRoute.path;
        router.push(newRoute);
    }
};
</script>

<style lang="scss" scoped>
.n-flex {
    background: var(--tab-color);
    padding: 8px;

    & .draggable-container {
        display: flex;

        & .draggable-container-item {
            display: flex;
            align-items: center;
            background: var(--base-color);
            color: var(--text-color-base);
            margin-right: 8px;
            height: 32px;
            padding: 4px 8px;
            border-radius: 4px;
            cursor: pointer;
        }

        & .draggable-container-item-active {
            color: var(--primary-color);
        }
    }
}
</style>
