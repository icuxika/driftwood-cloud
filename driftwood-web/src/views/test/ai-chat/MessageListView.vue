<template>
    <div>
        <div
            v-for="item in list"
            :key="item.id"
            style="display: flex"
            :style="{ flexDirection: item.left ? 'row' : 'row-reverse' }"
        >
            <n-card embedded :bordered="false" size="large">
                {{ item.message }}
            </n-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, ref, Ref } from "vue";

interface Message {
    id: string;
    message: string;
    left: boolean;
}

interface Props {
    messageList: Message[];
}

const props = defineProps<Props>();

interface X {
    id: string;
    message: Ref<string>;
    left: boolean;
}
const list = computed(() => {
    const r: X[] = [];

    if (props.messageList) {
        props.messageList.forEach((item) => {
            r.push({
                id: item.id,
                message: ref(item.message),
                left: item.left,
            });
        });
    }

    return r;
});
</script>
<style lang="scss" scoped>
.n-card {
    max-width: 50%;
}
</style>
