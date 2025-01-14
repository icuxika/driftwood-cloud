<template>
    <div class="container">
        <n-result
            status="404"
            title="404 资源不存在"
            description="生活总归带点荒谬"
        >
            <template #footer>
                <n-button @click="router.go(-1)">返回</n-button>
            </template>
        </n-result>
    </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
const router = useRouter();
</script>

<style lang="scss" scoped>
.container {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
