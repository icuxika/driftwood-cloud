<template>
	<div>
		<n-upload :custom-request="customRequest" @remove="handleRemove">
			<n-button>上传文件</n-button>
		</n-upload>
		<n-button :loading="loading" type="info" @click="download">
			下载
		</n-button>
	</div>
</template>

<script setup lang="ts">
import { fileService } from "@/api/modules/admin/file";
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
const { downloadFile } = useFile();
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
		.then((id) => {
			if (id) {
				fileIdMap[file.id] = id;
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

const download = () => {
	loading.value = true;
	fileService
		.downloadFile(1)
		.then((response) => {
			loading.value = false;
			downloadFile(response);
		})
		.catch((error) => {
			loading.value = false;
		});
};
</script>

<style lang="scss" scoped></style>
