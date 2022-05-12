<template>
	<div class="container">
		<n-layout-header bordered>
			<div class="logo">
				<img src="../assets/logo.png" alt="" />
			</div>
			<ul class="menu">
				<li>
					<router-link to="/">Home</router-link>
				</li>
			</ul>
			<div class="extra">
				<n-switch
					:rail-style="railStyle"
					@update:value="handleUpdateValue"
				>
					<template #checked> 深色</template>
					<template #unchecked> 浅色</template>
				</n-switch>
				<a class="search" href="#">
					<svg class="icon" aria-hidden="true">
						<use xlink:href="#icon-search"></use>
					</svg>
				</a>
			</div>
		</n-layout-header>
		<n-layout-content>
			<n-layout has-sider class="basic-wrapper">
				<!-- 侧栏菜单 -->
				<SiderMenu />
				<n-layout>
					<!-- 导航 -->
					<MainHeader />
					<!-- 内容 -->
					<MainContent />
				</n-layout>
			</n-layout>
		</n-layout-content>
		<n-layout-footer bordered>成府路</n-layout-footer>
	</div>
</template>

<script setup lang="ts">
import { useLoadingBar, useMessage } from "naive-ui";
import SiderMenu from "@/layouts/components/SiderMenu.vue";
import MainHeader from "@/layouts/components/main/MainHeader.vue";
import MainContent from "@/layouts/components/main/MainContent.vue";
import { CSSProperties } from "vue";
import { useSystemStore } from "@/store/pinia/system";

const message = useMessage();

const systemStore = useSystemStore();

const loadingBar = useLoadingBar();
loadingBar.start();
setTimeout(() => {
	loadingBar.finish();
}, 1000);

const railStyle = ({
	focused,
	checked,
}: {
	focused: boolean;
	checked: boolean;
}) => {
	const style: CSSProperties = {};
	if (checked) {
		style.background = "#d03050";
		if (focused) {
			style.boxShadow = "0 0 0 2px #d0305040";
		}
	} else {
		style.background = "#2080f0";
		if (focused) {
			style.boxShadow = "0 0 0 2px #2080f040";
		}
	}
	return style;
};

const handleUpdateValue = (value: boolean) => {
	systemStore.setDarkThemeEnable(value);
};
</script>

<style lang="scss" scoped>
.container {
	height: 100vh;
	display: flex;
	flex-direction: column;
}

.n-layout-header {
	position: fixed;
	z-index: 1;
	display: flex;
	align-items: center;
	height: 48px;
	font-size: 16px;
	font-weight: bold;

	& .logo {
		display: flex;

		& img {
			margin-left: 16px;
			width: 32px;
		}
	}

	& .menu {
		display: flex;

		& li {
			margin-left: 16px;
		}
	}

	& .extra {
		margin-left: auto;

		& a {
			margin-left: 16px;
		}
	}
}

.n-layout-content {
	margin-top: 48px;

	& .basic-wrapper {
		height: 100%;
	}
}

.n-layout-footer {
	height: 48px;
}
</style>
