<template>
	<uploader
		ref="uploaderRef"
		:options="options"
		:file-status-text="statusText"
		class="partial-file-uploader"
		@file-added="fileAdded"
		@file-complete="fileComplete"
		@complete="complete"
		@file-success="fileSuccess"
	></uploader>
</template>

<script setup lang="ts">
const options = {
	target: (file: any, chunk: any, mode: any) => {},
	chunkSize: 5 * 1024 * 1024,
	forceChunkSize: true,
	query: (file: any, chunk: any, mode: any) => {
		return {
			partNumber: (chunk as any).offset + 1,
		};
	},
	method: "octet",
	uploadMethod: "PUT",
};
const statusText = {
	success: "成功了",
	error: "出错了",
	uploading: "上传中",
	paused: "暂停中",
	waiting: "等待中",
};
const fileAdded = (file: any, event: any) => {
	// 单个文件添加，获取文件分片上传路径
};
const fileComplete = (rootFile: any) => {};
const complete = () => {};
const fileSuccess = (rootFile: any, file: any, message: any, chunk: any) => {
	// 单个文件上传成功，调用文件分片合并
};
</script>

<style lang="scss" scoped>
.partial-file-uploader {
	width: 880px;
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
