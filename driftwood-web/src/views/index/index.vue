<template>
    <div>
        <FileUpload />
        <button type="button" @click="openGithubLoginWindow">GitHub</button>
        <button type="button" @click="openGiteeLoginWindow">Gitee</button>
        <button type="button" @click="testPromiseAll">testPromiseAll</button>
        <n-upload :custom-request="customRequest">
            <n-button>大文件分片</n-button>
        </n-upload>
    </div>
</template>
<script setup lang="ts">
import FileUpload from "@/components/FileUpload.vue";
import { useFile } from "@/hooks/use-file";
import { useUserStore } from "@/store/user/user";
import { UploadCustomRequestOptions } from "naive-ui";
import { onMounted, onUnmounted } from "vue";
const userStore = useUserStore();
const { cutFile } = useFile();

const openGithubLoginWindow = () => {
    let width = 480;
    let height = 480;
    let positionLeft = (window.screen.availWidth - width) / 2;
    let positionTop = (window.screen.availHeight - height) / 2;
    window.open(
        "https://github.com/login/oauth/authorize?client_id=" +
            import.meta.env.VITE_GITHUB_CLIENT_ID +
            "&redirect_uri=" +
            import.meta.env.VITE_GITHUB_REDIRECT_URI +
            "&scope=user,public_repo",
        "login",
        "height=" +
            height +
            ", width= " +
            height +
            ", top=" +
            positionTop +
            ", left=" +
            positionLeft +
            ", toolbar=no, menubar=no, scrollbars=no, resizable=no ,location=no, status=no"
    );
};

const openGiteeLoginWindow = () => {
    let width = 480;
    let height = 480;
    let positionLeft = (window.screen.availWidth - width) / 2;
    let positionTop = (window.screen.availHeight - height) / 2;
    window.open(
        "https://gitee.com/oauth/authorize?client_id=" +
            import.meta.env.VITE_GITEE_CLIENT_ID +
            "&redirect_uri=" +
            import.meta.env.VITE_GITEE_REDIRECT_URI +
            "&response_type=code&scope=user_info",
        "login",
        "height=" +
            height +
            ", width= " +
            height +
            ", top=" +
            positionTop +
            ", left=" +
            positionLeft +
            ", toolbar=no, menubar=no, scrollbars=no, resizable=no ,location=no, status=no"
    );
};

const listener = (e: MessageEvent) => {
    if (typeof e.data === "string") {
        console.log(e.data);
    }
};

const testPromiseAll = () => {
    Promise.all([userStore.getUserInfo(), userStore.page({})])
        .then((res) => {
            console.log(res);
        })
        .catch((error) => {
            console.log(error);
        });
};

const customRequest = ({
    file,
    onFinish,
    onError,
    onProgress,
}: UploadCustomRequestOptions) => {
    let targetFile = file.file as File;
    console.log(targetFile.size);
    cutFile(file.file as File)
        .then((res) => {
            console.log(res);
            let all = res.reduce<number>((pre, cur) => {
                pre += cur.blob.size;
                return pre;
            }, 0);
            console.log(all);
        })
        .catch((error) => {
            console.log(error);
        });
};

onMounted(() => {
    window.addEventListener("message", listener);
});

onUnmounted(() => {
    window.removeEventListener("message", listener);
});
</script>
<style lang="scss" scoped></style>
