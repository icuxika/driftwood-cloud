<template>
	<div>
		<n-upload :custom-request="customRequest" @remove="handleRemove">
			<n-button>上传文件</n-button>
		</n-upload>
		<n-button type="info" @click="download"> 下载 </n-button>
	</div>
</template>

<script setup lang="ts">
import {
	UploadCustomRequestOptions,
	UploadFileInfo,
	useMessage,
} from "naive-ui";
import { useFileStore } from "@/store/pinia/admin/file";
import { fileService } from "@/api/modules/admin/file";

const message = useMessage();
const fileStore = useFileStore();

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
	fileService.downloadFile(2).then((response) => {
		const url = window.URL.createObjectURL(new Blob([response.data]));
		const link = document.createElement("a");
		link.href = url;
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
		URL.revokeObjectURL(url);
	});
};
</script>

<style lang="scss" scoped></style>
