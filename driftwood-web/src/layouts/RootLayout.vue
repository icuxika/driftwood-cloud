<template>
	<n-loading-bar-provider>
		<n-notification-provider>
			<n-config-provider
				:theme="systemStore.naiveUITheme"
				:locale="systemStore.naiveUILocale"
				:date-locale="systemStore.naiveUIDateLocale"
			>
				<n-dialog-provider>
					<router-view />
				</n-dialog-provider>
			</n-config-provider>
		</n-notification-provider>
	</n-loading-bar-provider>
</template>

<script setup lang="ts">
import { useMessage } from "naive-ui";
import { useSystemStore } from "@/store/pinia/system";

// 在 setup 外使用 Message，挂载到window上，需要确保App.vue中本组件在n-message-provider内部
// https://www.naiveui.com/zh-CN/os-theme/components/message#在-setup-外使用
window.$message = useMessage();

const systemStore = useSystemStore();
</script>

<style scoped></style>
