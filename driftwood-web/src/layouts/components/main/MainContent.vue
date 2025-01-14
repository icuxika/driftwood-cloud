<template>
    <n-el class="main-content-el">
        <n-layout v-if="!hideNLayout" has-sider :native-scrollbar="false">
            <div class="main-content">
                <router-view v-slot="{ Component }">
                    <template v-if="Component">
                        <transition mode="out-in">
                            <keep-alive :include="navStore.keepAliveInclude">
                                <suspense>
                                    <component :is="Component"></component>
                                    <template #fallback>
                                        <div>Loading...</div>
                                    </template>
                                </suspense>
                            </keep-alive>
                        </transition>
                    </template>
                </router-view>
            </div>
        </n-layout>
        <div v-else class="main-content">
            <router-view v-slot="{ Component }">
                <template v-if="Component">
                    <transition mode="out-in">
                        <keep-alive :include="navStore.keepAliveInclude">
                            <suspense>
                                <component :is="Component"></component>
                                <template #fallback>
                                    <div>Loading...</div>
                                </template>
                            </suspense>
                        </keep-alive>
                    </transition>
                </template>
            </router-view>
        </div>
    </n-el>
</template>

<script setup lang="ts">
import { useNavStore } from "@/store/nav";
import { computed } from "vue";
import { useRoute } from "vue-router";
const navStore = useNavStore();
const route = useRoute();
const hideNLayout = computed(() => {
    return route.path === "/test/ai-chat";
});
</script>

<style lang="scss">
.main-content-el {
    width: 100%;
    height: 100%;
    padding: 0px 8px;
    background-color: var(--tab-color);
    & > .n-layout {
        height: 100%;
    }
    .main-content {
        width: 100%;
        height: 100%;
        padding: 8px;
        background-color: var(--base-color);
    }
}
</style>
