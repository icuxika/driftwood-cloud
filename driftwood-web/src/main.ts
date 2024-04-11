import App from "@/App.vue";
import router from "@/router";
import { createPinia } from "pinia";
import { createApp } from "vue";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import uploader from "vue-simple-uploader";
import "vue-simple-uploader/dist/style.css";

const app = createApp(App);
app.use(router);
app.use(createPinia());
app.use(uploader);
router.isReady().then(() => {
    app.mount("#app");
});
