<template>
    <div>
        <div
            v-for="item in list"
            :key="item.id"
            style="display: flex; padding: 8px 0"
            :style="{ flexDirection: item.left ? 'row' : 'row-reverse' }"
        >
            <div
                class="bubble-message"
                :class="{
                    'bubble-message-left': item.left,
                    'bubble-message-right': !item.left,
                }"
            >
                <p>{{ item.message }}</p>
            </div>
        </div>
        <n-card embedded :bordered="true" size="large">
            <div v-html="markdown"></div>
        </n-card>
    </div>
</template>

<script setup lang="ts">
import { marked } from "marked";
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

const markdown = ref<string>("");
const render2Markdown = (id: string) => {
    markdown.value = marked.parse(
        list.value.filter((item) => item.id === id)[0].message.value
    ) as string;
};
defineExpose({
    render2Markdown,
});
</script>
<style lang="scss" scoped>
.bubble-message {
    max-width: 80%;
    border-radius: 16px;
    padding: 8px 24px;
    font-size: 16px;
    position: relative;
}

.bubble-message-left {
    $left-color: white;
    color: black;
    background-color: $left-color;
    &::after {
        content: "";
        width: 32px;
        height: 32px;
        background: $left-color;
        mask-repeat: no-repeat;
        mask-size: contain;
        mask-image: url(/download/message-decorate-left.svg);

        position: absolute;
        left: 0;
        top: 50%;
        transform: translate(-40%, -50%);
    }
}

.bubble-message-right {
    $right-color: #1772f6;
    color: white;
    background-color: $right-color;
    &::after {
        content: "";
        width: 32px;
        height: 32px;
        background: $right-color;
        mask-repeat: no-repeat;
        mask-size: contain;
        mask-image: url(/download/message-decorate-right.svg);

        position: absolute;
        right: 0;
        top: 50%;
        transform: translate(40%, -50%);
    }
}
</style>
