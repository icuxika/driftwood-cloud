<template>
    <n-space>
        <Draggable
            v-model="navRouteList"
            class="draggable-container"
            tag="transition-group"
            :component-data="{
                tag: 'div',
                name: !dragging ? 'flip-list' : null,
            }"
            v-bind="dragOptions"
            item-key="path"
            @start="dragging = true"
            @end="dragging = false"
        >
            <template #item="{ element }">
                <div
                    class="draggable-container-item"
                    :class="{
                        'draggable-container-item-active':
                            element.path === activePath,
                    }"
                    @click.stop="goto(element)"
                >
                    <span>{{ element.meta.title }}</span>
                    <n-icon
                        v-if="!element.meta.fixed"
                        size="16"
                        @click.stop="closeTab(element)"
                    >
                        <CloseIcon />
                    </n-icon>
                </div>
            </template>
        </Draggable>
    </n-space>
</template>

<script setup lang="ts">
import { NavRoute, useNavStore } from "@/store/nav";
import { CloseOutline as CloseIcon } from "@vicons/ionicons5";
import { computed, ref, watch } from "vue";
import { _RouteLocationBase, useRoute, useRouter } from "vue-router";
import Draggable from "zhyswan-vuedraggable";

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

// 是否处于拖拽，此判断影响是否给<transition-group>指定name属性，目前来自于vue.draggable.next的示例(https://github.com/SortableJS/vue.draggable.next/blob/master/example/components/transition-example-2.vue)
// 实际测试，不使用列表的移动过渡(.flip-list-move),底层的Sortable似乎已经提供应有的效果了，待后期测试
const dragging = ref(false);

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
.n-space {
    background: #e9ebec;
    padding: 8px;

    & .draggable-container {
        display: flex;

        & .draggable-container-item {
            display: flex;
            align-items: center;
            background: white;
            color: black;
            margin-right: 8px;
            height: 32px;
            padding: 4px 8px;
            border-radius: 4px;
            cursor: pointer;
        }

        & .draggable-container-item-active {
            color: dodgerblue;
        }
    }
}

.flip-list-move {
    transition: transform 0.2s ease;
}

.ghost {
    opacity: 0.5;
}
</style>
