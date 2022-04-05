<template>
  <div class="container">
    <n-layout-header bordered>
      <div class="logo">
        <img src="../assets/logo.png" alt="">
      </div>
      <ul class="menu">
        <li>
          <router-link to="/">Home</router-link>
        </li>
      </ul>
      <div class="extra">
        <a class="search" href="#">
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#icon-search"></use>
          </svg>
        </a>
        <router-link class="sign-in" to="/login">Sign in</router-link>
        <a class="sign-up" href="#">Sign up</a>
      </div>
    </n-layout-header>
    <n-layout-content>
      <n-button>naive-ui</n-button>
      <n-card>
        <n-space>
          <n-button @click="store.dispatch('index/setEnableDarkTheme', true)">深色</n-button>
          <n-button @click="store.dispatch('index/setEnableDarkTheme', false)">浅色</n-button>
        </n-space>
      </n-card>
      <n-layout has-sider class="basic-wrapper">
        <SiderMenu/> <!-- 侧栏菜单 -->
        <n-layout>
          <MainHeader/>
          <MainContent />
        </n-layout>
      </n-layout>
    </n-layout-content>
    <n-layout-footer bordered>成府路</n-layout-footer>
  </div>
</template>

<script setup lang="ts">
import {useStore} from "../store";
import {useLoadingBar, useMessage} from "naive-ui";
import SiderMenu from "../layouts/components/SiderMenu.vue";
import MainHeader from "../layouts/components/main/MainHeader.vue";
import MainContent from "../layouts/components/main/MainContent.vue";

const message = useMessage();

const store = useStore();
// store.dispatch("user/getUserInfo");
// store.dispatch("user/page", {
// 	size: 10,
// 	page: 0,
// 	sort: "id,desc",
// 	phone: "18752065699"
// });

const loadingBar = useLoadingBar();
loadingBar.start();
setTimeout(() => {
	loadingBar.finish();
}, 1000);
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
      margin-right: 16px;
    }

    & .sign-in {
      background-color: #ea7d9f;
      padding: 8px 16px;
      border-radius: 8px;
      color: white;
    }

    & .sign-up {
      background-color: #ea7d9f;
      padding: 8px 16px;
      border-radius: 8px;
      color: white;
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
