<template>
    <div class="container">
        <n-layout :native-scrollbar="false">
            <div class="message-wrapper">
                <MessageListView
                    ref="messageListView"
                    :message-list="messageList"
                />
            </div>
        </n-layout>
        <div ref="inputTextareaWrapper" class="send-wrapper">
            <textarea
                ref="inputTextarea"
                class="input-textarea"
                rows="1"
                v-model="input"
                @keyup.enter="confirm"
            ></textarea>
            <div>
                <n-button
                    type="primary"
                    :loading="confirmBtnLoading"
                    @click="confirm"
                    >发送</n-button
                >
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { chatClientService } from "@/api/modules/bailian/chat-client";
import { useTrick } from "@/hooks/use-trick";
import { getCurrentInstance, onMounted, ref, useTemplateRef, watch } from "vue";
import MessageListView from "./MessageListView.vue";
const { debounce } = useTrick();

const messageListView = useTemplateRef("messageListView");

interface Message {
    id: string;
    message: string;
    left: boolean;
}

const messageList = ref<Message[]>([]);

const inputTextareaWrapper = useTemplateRef("inputTextareaWrapper");
const inputTextarea = useTemplateRef("inputTextarea");
const initialTextareaScrollHeight = ref(0);
const input = ref<string>("");
const debounceInputChange = debounce(() => {
    if (inputTextarea.value) {
        inputTextarea.value.style.height = "auto";
        inputTextarea.value.style.height =
            inputTextarea.value.scrollHeight + "px";
        if (inputTextareaWrapper.value) {
            inputTextareaWrapper.value.style.marginTop = `-${inputTextarea.value.scrollHeight - initialTextareaScrollHeight.value}px`;
        }
    }
}, 200);
watch(
    input,
    (input, prevInput) => {
        debounceInputChange();
    },
    {
        flush: "post",
    }
);

const internalInstance = getCurrentInstance();
const generateId = () => {
    return (
        "v" +
        "-" +
        (internalInstance as any).ids[0] +
        (internalInstance as any).ids[1]++
    );
};

const confirmBtnLoading = ref<boolean>(false);
const send = (id: string, text: string) => {
    confirmBtnLoading.value = true;
    chatClientService
        .streamChat(text)
        .then(async (res) => {
            const reader = (res.data as ReadableStream)
                .pipeThrough(new TextDecoderStream())
                .getReader();
            while (true) {
                const { done, value } = await reader.read();
                if (done) {
                    break;
                }
                messageList.value.filter((item) => item.id === id)[0].message +=
                    value;
            }
            // https://github.com/MeSilicon7/LexiStreamKit
            messageListView.value?.render2Markdown(id);
            confirmBtnLoading.value = false;
        })
        .catch((err) => {
            messageList.value.filter((item) => item.id === id)[0].message +=
                err.message;
            confirmBtnLoading.value = false;
        });
};

const confirm = async () => {
    const userInput = input.value;
    input.value = "";

    const rightId = generateId();
    const msg: Message = {
        id: rightId,
        message: userInput,
        left: false,
    };
    messageList.value.push(msg);

    const leftId = generateId();
    const leftMsg: Message = {
        id: leftId,
        message: "",
        left: true,
    };
    messageList.value.push(leftMsg);
    send(leftId, userInput);
};

onMounted(() => {
    if (inputTextarea.value) {
        initialTextareaScrollHeight.value = inputTextarea.value.scrollHeight;
    }
});
</script>

<style lang="scss" scoped>
.container {
    height: 100%;
    .n-layout {
        height: calc(100% - 128px);
        border: 2px solid var(--border-color);
    }
    .message-wrapper {
        padding: 8px 32px;
        background-color: rgba(0, 0, 0, 0.5);
    }
    .send-wrapper {
        position: relative;
        left: calc(50% - 360px);
        top: 16px;
        width: 720px;

        display: flex;
        flex-direction: column;
        align-items: center;
        background-color: rgb(32, 30, 30);
        padding: 16px;
        border-radius: 8px;

        .input-textarea {
            border: none;
            outline: none;
            font-size: 16px;
            width: 100%;
            resize: none;
            line-height: 2;
            background-color: transparent;
            color: white;

            &::-webkit-scrollbar {
                display: none;
            }
        }
    }
}
</style>
