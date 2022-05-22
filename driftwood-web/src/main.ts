import { createApp } from "vue";
import App from "@/App.vue";
import router from "@/router";
import { key, store } from "@/store";
import { createPinia } from "pinia";

import {
	create,
	NButton,
	NCard,
	NConfigProvider,
	NDataTable,
	NDialogProvider,
	NDropdown,
	NForm,
	NFormItem,
	NFormItemRow,
	NGi,
	NGrid,
	NIcon,
	NInput,
	NInputGroup,
	NLayout,
	NLayoutContent,
	NLayoutFooter,
	NLayoutHeader,
	NLayoutSider,
	NLoadingBarProvider,
	NMenu,
	NMessageProvider,
	NModal,
	NPagination,
	NSelect,
	NSpace,
	NSwitch,
	NTabPane,
	NTabs,
	NTag,
	NTime,
	NTimeline,
	NTimelineItem,
	NTree,
	NNotificationProvider,
	NAvatar,
} from "naive-ui";

const naive = create({
	components: [
		NDataTable,
		NSelect,
		NFormItem,
		NDropdown,
		NTree,
		NTag,
		NIcon,
		NMenu,
		NLayoutSider,
		NSwitch,
		NInputGroup,
		NInput,
		NTabs,
		NTabPane,
		NForm,
		NFormItemRow,
		NModal,
		NPagination,
		NTime,
		NGrid,
		NGi,
		NTimeline,
		NTimelineItem,
		NMessageProvider,
		NDialogProvider,
		NConfigProvider,
		NLoadingBarProvider,
		NButton,
		NSpace,
		NCard,
		NLayout,
		NLayoutHeader,
		NLayoutContent,
		NLayoutFooter,
		NNotificationProvider,
		NAvatar,
	],
});

import {
	create as createVUI,
	VBanner,
	VButton,
} from "@icuxika/vue-scaffold-ui";
import "@icuxika/vue-scaffold-ui/style.css";

const vueScaffoldUI = createVUI({
	components: [VBanner, VButton],
});

const app = createApp(App);
app.use(router);
app.use(store, key);
app.use(createPinia());
app.use(naive);
app.use(vueScaffoldUI);
router.isReady().then(() => {
	app.mount("#app");
});
