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
        <n-upload :custom-request="multipartUpload">
            <n-button>大文件分片上传</n-button>
        </n-upload>
    </div>
</template>

<script setup lang="ts">
import { ApiDataResponse, resolveAxiosResult } from "@/api";
import { fileService, UploadPartResult } from "@/api/modules/admin/file";
import { useFile } from "@/hooks/use-file";
import { useFileStore } from "@/store/admin/file";
import {
    UploadCustomRequestOptions,
    UploadFileInfo,
    useMessage,
} from "naive-ui";
import { ref } from "vue";

const { cutFile } = useFile();
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

const multipartUpload = async ({
    file,
    onFinish,
    onError,
    onProgress,
}: UploadCustomRequestOptions) => {
    // 初始化分片上传
    let initiateMultipartUploadResult = await resolveAxiosResult(() =>
        fileService.initiateMultipartUpload(file.name)
    );
    if (initiateMultipartUploadResult) {
        console.log(
            "initiateMultipartUploadResult: ",
            initiateMultipartUploadResult
        );
        // 对文件进行分片
        let chunks = await cutFile(file.file as File);
        // 上传各个文件分片
        let totalProgress = 0;
        let lastProgress = 0;
        const uploadPartTasks: ApiDataResponse<UploadPartResult>[] = chunks.map(
            (chunk) => {
                const chunkSize = chunk.end - chunk.start;
                const chunkWeight = chunkSize / (file.file as File).size;

                return fileService.uploadPart(
                    {
                        part: chunk.blob,
                        objectName: initiateMultipartUploadResult.key,
                        uploadId: initiateMultipartUploadResult.uploadId,
                        partNumber: chunk.index + 1,
                        partSize: chunk.end - chunk.start,
                        fileOffset: chunk.start,
                        md5Digest: chunk.hash,
                    },
                    (percent) => {
                        const weightProcess = percent * chunkWeight;
                        totalProgress += weightProcess;
                        totalProgress = Math.min(totalProgress, 100);
                        if (totalProgress - lastProgress > 1) {
                            lastProgress = totalProgress;
                            onProgress({ percent: totalProgress });
                        }
                    }
                );
            }
        );
        const uploadPartResults = await Promise.all(uploadPartTasks);
        onProgress({ percent: totalProgress });
        console.log("uploadPartResults: ", uploadPartResults);
        const result = await fileService.completeMultipartUpload({
            originalFilename: file.name,
            objectName: initiateMultipartUploadResult.key,
            uploadId: initiateMultipartUploadResult.uploadId,
            partETags: uploadPartResults.map((uploadPartResult) => {
                return {
                    partNumber: uploadPartResult.data.data!.partNumber,
                    tag: uploadPartResult.data.data!.etag,
                };
            }),
        });
        console.log("result: ", result);
        message.success(`文件分片上传已完成[${result.data.data?.id}]`);
    }
};
</script>

<style lang="scss" scoped></style>
