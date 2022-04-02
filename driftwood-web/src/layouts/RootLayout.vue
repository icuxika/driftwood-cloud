<template>
  <n-loading-bar-provider>
    <n-config-provider :theme="theme" :locale="locale" :date-locale="dateLocale">
      <n-dialog-provider>
        <router-view/>
      </n-dialog-provider>
    </n-config-provider>
  </n-loading-bar-provider>
</template>

<script setup lang="ts">
import {computed} from "vue";
import {useStore} from "../store";
import {useMessage} from "naive-ui";

// 在 setup 外使用 Message，挂载到window上，需要确保App.vue中本组件在n-message-provider内部
// https://www.naiveui.com/zh-CN/os-theme/components/message#在-setup-外使用
window.$message = useMessage();

const store = useStore();
const theme = computed(() => store.state.index.naiveUITheme);
const locale = computed(() => store.state.index.naiveUILocale);
const dateLocale = computed(() => store.state.index.naiveUIDateLocale);
</script>

<style scoped>

</style>
