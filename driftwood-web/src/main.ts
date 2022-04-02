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
    NForm,
    NFormItemRow,
    NGi,
    NGrid,
    NInput,
    NInputGroup,
    NLayout,
    NLayoutContent,
    NLayoutFooter,
    NLayoutHeader,
    NLoadingBarProvider,
    NMessageProvider,
    NModal,
    NPagination,
    NSpace,
    NSwitch,
    NTabPane,
    NTabs,
    NTime,
    NTimeline,
    NTimelineItem
} from "naive-ui";

const naive = create({
	components: [NSwitch, NInputGroup, NInput, NTabs, NTabPane, NForm, NFormItemRow, NModal, NPagination, NTime, NGrid, NGi, NTimeline, NTimelineItem, NMessageProvider, NDialogProvider, NConfigProvider, NLoadingBarProvider, NButton, NSpace, NCard, NLayout, NLayoutHeader, NLayoutContent, NLayoutFooter]
});

const app = createApp(App);
app.use(router);
app.use(store, key);
app.use(naive);
app.mount("#app");
