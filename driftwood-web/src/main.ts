import App from "@/App.vue";
import router from "@/router";
import { createPinia } from "pinia";
import { createApp } from "vue";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import uploader from "vue-simple-uploader";
import "vue-simple-uploader/dist/style.css";

import {
	NAvatar,
	NButton,
	NCard,
	NColorPicker,
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
	NNotificationProvider,
	NPagination,
	NSelect,
	NSpace,
	NSwitch,
	NTabPane,
	NTabs,
	NTag,
	NThemeEditor,
	NTime,
	NTimeline,
	NTimelineItem,
	NTree,
	NUpload,
	create,
} from "naive-ui";

const naive = create({
	components: [
		NThemeEditor,
		NColorPicker,
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
		NUpload,
	],
});

const app = createApp(App);
app.use(router);
app.use(createPinia());
app.use(naive);
app.use(uploader);
router.isReady().then(() => {
	app.mount("#app");
});
