<template>
	<uploader
		ref="uploaderRef"
		:options="options"
		:auto-start="false"
		:file-status-text="statusText"
		class="partial-file-uploader"
		@file-added="fileAdded"
		@file-complete="fileComplete"
		@complete="complete"
		@file-success="fileSuccess"
	>
		<uploader-unsupport></uploader-unsupport>
		<uploader-drop>
			<p>拖动文件到此处</p>
			<uploader-btn>选择文件</uploader-btn>
		</uploader-drop>
		<uploader-list></uploader-list>
	</uploader>
</template>

<script setup lang="ts">
/* eslint-disable @typescript-eslint/no-explicit-any */
import { ref } from "vue";
import { useMinioFileStore } from "@/store/pinia/file/minio-file";

const minioFileStore = useMinioFileStore();
const uploaderRef = ref<any>(null);
const options = {
	target: (file: any, chunk: any, mode: any) => {
		return file.chunkUrlData["chunk_" + chunk.offset];
	},
	singleFile: true, // 单文件上传
	chunkSize: 5 * 1024 * 1024,
	forceChunkSize: true,
	method: "octet",
	uploadMethod: "PUT",
	testChunks: false,
	processParams: (params: any) => {
		// 不删除以下参数，会遇到 SignatureDoesNotMatch 错误
		delete params["chunkNumber"];
		delete params["chunkSize"];
		delete params["currentChunkSize"];
		delete params["totalSize"];
		delete params["identifier"];
		delete params["filename"];
		delete params["relativePath"];
		delete params["totalChunks"];
		return params;
	},
};
const statusText = {
	success: "成功了",
	error: "出错了",
	uploading: "上传中",
	paused: "暂停中",
	waiting: "等待中",
};
const fileAdded = async (file: any, event: any) => {
	// 单个文件添加，获取文件分片上传路径
	const fileName = (file as File).name;
	const chunkNumber = file.chunks.length;
	let map = await minioFileStore
		.createMultipartUpload(fileName, chunkNumber)
		.then();
	if (map) {
		file.chunkUrlData = map;
		uploaderRef.value.uploader.upload();
	}
};
const fileComplete = (rootFile: any) => {};
const complete = () => {};
const fileSuccess = (rootFile: any, file: any, message: any, chunk: any) => {
	// 单个文件上传成功，调用文件分片合并
	const fileName = file.name;
	const uploadId = file.chunkUrlData.uploadId;
	minioFileStore.completeMultipartUpload(fileName, uploadId).then();
};
</script>

<style lang="scss" scoped>
.partial-file-uploader {
	//width: 880px;
	padding: 16px;
	margin: 40px auto 0;
	font-size: 12px;
	box-shadow: 0 0 10px rgba(0, 0, 0, 0.4);
}
.partial-file-uploader .uploader-btn {
	margin-right: 4px;
}
.partial-file-uploader .uploader-list {
	max-height: 440px;
	overflow: auto;
	overflow-x: hidden;
}
</style>
