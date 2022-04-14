<template>
	<n-message-provider>
		<RootLayout />
	</n-message-provider>
</template>

<script setup lang="ts">
import RootLayout from "./layouts/RootLayout.vue";
</script>

<style lang="scss">
* {
	margin: 0;
	padding: 0;
	// 使边框和内边距的值包含在width内
	box-sizing: border-box;
}

ul,
ol {
	list-style: none;
}

a {
	text-decoration: none;
	color: inherit;

	&:hover {
		cursor: pointer;
		text-decoration: underline;
	}
}

body {
	height: 100vh;
}

// iconfont.cn
.icon {
	width: 1em;
	height: 1em;
	vertical-align: -0.15em;
	fill: currentColor;
	overflow: hidden;
}

#app {
	font-family: Avenir, Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
}
</style>
