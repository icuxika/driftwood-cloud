<template>
  <div class="container">
    <n-card title="登录">
      <n-tabs
          default-value="login1"
          @before-leave="handleBeforeLeave"
          @update:value="handleUpdateValue"
          size="large"
          justify-content="space-evenly">
        <n-tab-pane name="login1" tab="密码">
          <n-form>
            <n-form-item-row label="用户名">
              <n-input v-model:value="username"/>
            </n-form-item-row>
            <n-form-item-row label="密码">
              <n-input
                  type="password"
                  show-password-on="mousedown"
                  placeholder="密码"
                  :maxlength="64"
                  v-model:value="password"
              />
            </n-form-item-row>
            <n-form-item-row label="记住我">
              <n-switch v-model:value="rememberMe"/>
            </n-form-item-row>
          </n-form>
          <n-button type="primary" block secondary strong @click="submitLogin">
            登录
          </n-button>
        </n-tab-pane>
        <n-tab-pane name="login2" tab="短信">
          <n-form>
            <n-form-item-row label="手机号">
              <n-input v-model:value="phone"/>
            </n-form-item-row>
            <n-form-item-row label="验证码">
              <n-input-group>
                <n-input v-model:value="code"/>
                <n-button type="primary">
                  发送验证码
                </n-button>
              </n-input-group>
            </n-form-item-row>
            <n-form-item-row label="记住我">
              <n-switch v-model:value="rememberMe"/>
            </n-form-item-row>
          </n-form>
          <n-button type="primary" block secondary strong @click="submitLogin">
            登录
          </n-button>
        </n-tab-pane>
      </n-tabs>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import {ref} from "vue";
import {useMessage} from "naive-ui";
import {useStore} from "../store";
import {useRoute, useRouter} from "vue-router";

const message = useMessage();
const store = useStore();
const route = useRoute();
const router = useRouter();

enum LoginType {
  PASSWORD,
  PHONE
}

let loginType = LoginType.PASSWORD;
// 密码登录
const username = ref("");
const password = ref("");
// 短信登录
const phone = ref("");
const code = ref("");
const rememberMe = ref(false);

const submitLogin = () => {
  switch (loginType) {
    case LoginType.PASSWORD:
      console.log(username.value);
      console.log(password.value);
      console.log(rememberMe.value);
      store.dispatch("auth/loginByPassword", {
        username: username.value,
        password: password.value
      }).then(() => {
        router.replace({path: route.query.redirect as string || "/"});
      }).catch(error => {
        console.log(error);
      });
      break;

    case LoginType.PHONE:
      console.log(phone.value);
      console.log(code.value);
      console.log(rememberMe.value);
      store.dispatch("auth/loginByPhone", {
        phone: phone.value,
        code: code.value
      });
      break;
  }
};

const handleBeforeLeave = (tabName: string) => {
  switch (tabName) {
    case "login1":
      message.info("使用密码进行登录", {duration: 500});
      loginType = LoginType.PASSWORD;
      break;
    case "login2":
      message.info("使用短信进行登录", {duration: 500});
      loginType = LoginType.PHONE;
      break;
  }
  return true;
};

const handleUpdateValue = (tabName: string) => {
};
</script>

<style lang="scss" scoped>
.container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 48px;
}
</style>
