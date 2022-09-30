<template>
	<n-upload :custom-request="customRequest" @remove="handleRemove">
		<n-button>上传文件</n-button>
	</n-upload>
</template>

<script setup lang="ts">
import {
	UploadCustomRequestOptions,
	UploadFileInfo,
	useMessage,
} from "naive-ui";
import { useFileStore } from "@/store/pinia/admin/file";

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
</script>

<style lang="scss" scoped></style>
