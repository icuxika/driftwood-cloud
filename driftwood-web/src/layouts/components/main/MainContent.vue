<template>
    <n-el class="main-content-el">
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
    </n-el>
</template>

<script setup lang="ts">
import { useNavStore } from "@/store/nav";
const navStore = useNavStore();
</script>

<style lang="scss">
.main-content-el {
    width: 100%;
    padding: 0px 8px;
    background-color: var(--tab-color);

    & .main-content {
        padding: 8px;
        background-color: var(--base-color);
    }
}
</style>
