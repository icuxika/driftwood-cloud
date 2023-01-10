<template>
	<div>
		<FileUpload />
		<button type="button" @click="openGithubLoginWindow">GitHub</button>
		<button type="button" @click="openGiteeLoginWindow">Gitee</button>
	</div>
</template>
<script setup lang="ts">
import FileUpload from "@/components/FileUpload.vue";
import { onMounted } from "vue";
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

onMounted(() => {
	window.addEventListener("message", listener);
});
</script>
<style lang="scss" scoped></style>
