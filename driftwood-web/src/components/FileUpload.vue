<template>
    <div>
        <n-upload :custom-request="customRequest" @remove="handleRemove">
            <n-button>上传文件</n-button>
        </n-upload>
        <n-button :loading="loading" type="info" @click="download">
            下载
        </n-button>
        <n-button :loading="loading" type="info" @click="downloadByUrl">
            下载ByUrl
        </n-button>
    </div>
</template>

<script setup lang="ts">
import { useFile } from "@/hooks/use-file";
import { useFileStore } from "@/store/admin/file";
import {
    UploadCustomRequestOptions,
    UploadFileInfo,
    useMessage,
} from "naive-ui";
import { ref } from "vue";

const message = useMessage();
const fileStore = useFileStore();
const { downloadFile, downloadFileByUrl } = useFile();
const loading = ref(false);

// UploadFileInfo:id -> 后端 id
const fileIdMap: Record<string, number> = {};

const customRequest = ({
    file,
    onFinish,
    onError,
    onProgress,
}: UploadCustomRequestOptions) => {
    fileStore
        .uploadFile(file.file as File, (percent) => {
            onProgress({ percent: percent });
        })
        .then((vo) => {
            if (vo) {
                fileIdMap[file.id] = vo.id;
                message.success("文件上传成功");
                onFinish();
            }
        })
        .catch((error) => {
            message.error(error);
            onError();
        });
};

const handleRemove = ({
    file,
    fileList,
}: {
    file: UploadFileInfo;
    fileList: Array<UploadFileInfo>;
}) => {
    const dataId = fileIdMap[file.id];
    if (dataId) {
        // 已上传成功的文件才能有后端 id
    }
    delete fileIdMap[file.id];
};

const download = async () => {
    console.log("开始下载");
    loading.value = true;
    fileStore
        .downloadFile(28, (percent) => {
            console.log(percent);
        })
        .then((response) => {
            loading.value = false;
            downloadFile(response);
        })
        .catch((error) => {
            loading.value = false;
        });
};

const downloadByUrl = async () => {
    const vo = await fileStore.getFilePath(28);
    if (vo) {
        const filePath =
            import.meta.env.VITE_APP_BASE_URL_PLACEHOLDER +
            "/admin/files/" +
            vo.filepath;
        const fileName = "new-" + vo.originalFilename;
        downloadFileByUrl(filePath, fileName);
    }
};
</script>

<style lang="scss" scoped></style>
