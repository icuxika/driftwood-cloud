import {createApp} from "vue";
import App from "./App.vue";
import router from "./router/index";
import {key, store} from "./store";

import {
	create,
	NButton,
	NCard,
	NConfigProvider,
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
	NTree
} from "naive-ui";

const naive = create({
	components: [NSelect, NFormItem, NDropdown, NTree, NTag, NIcon, NMenu, NLayoutSider, NSwitch, NInputGroup, NInput, NTabs, NTabPane, NForm, NFormItemRow, NModal, NPagination, NTime, NGrid, NGi, NTimeline, NTimelineItem, NMessageProvider, NDialogProvider, NConfigProvider, NLoadingBarProvider, NButton, NSpace, NCard, NLayout, NLayoutHeader, NLayoutContent, NLayoutFooter]
});

const app = createApp(App);
app.use(router);
app.use(store, key);
app.use(naive);
app.mount("#app");
