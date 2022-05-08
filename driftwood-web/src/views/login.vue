<template>
	<div class="container">
		<n-card title="登录" :embedded="true">
			<n-tabs
				default-value="login1"
				size="large"
				justify-content="space-evenly"
				pane-class="login-tab"
				type="bar"
				:animated="true"
				@before-leave="handleBeforeLeave"
				@update:value="handleUpdateValue"
			>
				<n-tab-pane name="login1" :tab="tab1">
					<n-form>
						<n-form-item-row label="用户名">
							<n-input v-model:value="username" />
						</n-form-item-row>
						<n-form-item-row label="密码">
							<n-input
								v-model:value="password"
								type="password"
								show-password-on="mousedown"
								placeholder="密码"
								:maxlength="64"
							/>
						</n-form-item-row>
						<n-form-item-row label="记住我">
							<n-switch v-model:value="rememberMe" />
						</n-form-item-row>
					</n-form>
					<n-button
						type="primary"
						block
						secondary
						strong
						@click="submitLogin"
					>
						登录
					</n-button>
				</n-tab-pane>
				<n-tab-pane name="login2" :tab="tab2">
					<n-form>
						<n-form-item-row label="手机号">
							<n-input v-model:value="phone" />
						</n-form-item-row>
						<n-form-item-row label="验证码">
							<n-input-group>
								<n-input v-model:value="code" />
								<n-button
									type="primary"
									:disabled="getCodeButton.disabled"
									@click="getCode"
								>
									{{ getCodeButton.text }}
								</n-button>
							</n-input-group>
						</n-form-item-row>
						<n-form-item-row label="记住我">
							<n-switch v-model:value="rememberMe" />
						</n-form-item-row>
					</n-form>
					<n-button
						type="primary"
						block
						secondary
						strong
						@click="submitLogin"
					>
						登录
					</n-button>
				</n-tab-pane>
			</n-tabs>
		</n-card>
	</div>
</template>

<script setup lang="ts">
import { h, reactive, ref } from "vue";
import { NIcon, NSpace, useMessage } from "naive-ui";
import { useStore } from "@/store";
import { useRoute, useRouter } from "vue-router";
import {
	PasswordOutlined as PasswordIcon,
	SmsFilled as SmsIcon,
} from "@vicons/material";

const message = useMessage();
const store = useStore();
const route = useRoute();
const router = useRouter();

const tab1 = h(
	NSpace,
	{},
	{
		default: () => [h("h3", {}, "密码")],
	}
);
const tab2 = h(
	NSpace,
	{},
	{
		default: () => [h("h3", {}, "短信")],
	}
);

enum LoginType {
	PASSWORD,
	PHONE,
}

let loginType = LoginType.PASSWORD;
// 密码登录
const username = ref("icuxika");
const password = ref("rbj549232512");
// 短信登录
const phone = ref("12345678910");
const code = ref("123456");
const rememberMe = ref(false);

interface GetCodeButtonType {
	disabled: boolean;
	text: string;
	timer: NodeJS.Timer;
	duration: number;
}

const getCodeButton = reactive<GetCodeButtonType>({
	disabled: false,
	text: "获取验证码",
	timer: setInterval(() => {}),
	duration: 60,
});

const getCode = () => {
	getCodeButton.disabled = true;
	getCodeButton.timer && clearInterval(getCodeButton.timer);
	getCodeButton.timer = setInterval(() => {
		const temp = getCodeButton.duration--;
		getCodeButton.text = `${temp}秒`;
		if (temp <= 0) {
			clearInterval(getCodeButton.timer);
			getCodeButton.duration = 60;
			getCodeButton.text = "重新获取";
			getCodeButton.disabled = false;
		}
	}, 1000);
	message.info("短信已发送", { duration: 500 });
};

const submitLogin = () => {
	switch (loginType) {
		case LoginType.PASSWORD:
			console.log(username.value);
			console.log(password.value);
			console.log(rememberMe.value);
			store
				.dispatch("auth/loginByPassword", {
					username: username.value,
					password: password.value,
				})
				.then(() => {
					router.replace({
						path: (route.query.redirect as string) || "/",
					});
				})
				.catch((error) => {
					console.log(error);
				});
			break;

		case LoginType.PHONE:
			console.log(phone.value);
			console.log(code.value);
			console.log(rememberMe.value);
			store.dispatch("auth/loginByPhone", {
				phone: phone.value,
				code: code.value,
			});
			break;
	}
};

const handleBeforeLeave = (tabName: string) => {
	switch (tabName) {
		case "login1":
			message.info("使用密码进行登录", { duration: 500 });
			loginType = LoginType.PASSWORD;
			break;
		case "login2":
			message.info("使用短信进行登录", { duration: 500 });
			loginType = LoginType.PHONE;
			break;
	}
	return true;
};

const handleUpdateValue = (tabName: string) => {};
</script>

<style lang="scss" scoped>
.container {
	width: 100vw;
	height: 100vh;
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 0 48px;
	background: url("../assets/background.jpg") no-repeat;
	background-size: cover;

	.n-card {
		max-width: 600px;
	}
}
</style>
