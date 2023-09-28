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
						:loading="loginLoading"
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
						:loading="loginLoading"
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
import { UserInfoVO } from "@/api/modules/user/user";
import { useAuthStore } from "@/store/auth";
import { NAvatar, NSpace, useMessage, useNotification } from "naive-ui";
import { h, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const message = useMessage();
const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const notification = useNotification();

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

const loginLoading = ref(false);

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

const loginSuccessHandler = (userInfoVO: UserInfoVO) => {
	router.replace({
		path: (route.query.redirect as string) || "/",
	});
	loginLoading.value = false;
	let avatar =
		userInfoVO?.userProfile?.avatar ??
		"https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg";
	notification.create({
		title: "登录成功",
		content: `${userInfoVO.nickname}，欢迎回来！`,
		meta:
			new Date().toLocaleDateString().replaceAll("/", "-") +
			" " +
			new Date().toLocaleTimeString(),
		avatar: () =>
			h(NAvatar, {
				size: "small",
				round: true,
				src: avatar,
			}),
		onAfterLeave: () => {
			message.success("开始使用吧");
		},
	});
};

const submitLogin = () => {
	loginLoading.value = true;
	switch (loginType) {
		case LoginType.PASSWORD:
			authStore
				.loginByPassword(username.value, password.value)
				.then((userInfoVO) => {
					if (userInfoVO) {
						loginSuccessHandler(userInfoVO);
					}
				})
				.catch(() => {
					loginLoading.value = false;
					notification["error"]({
						content: "登录出错",
						meta: "请检查用户名或者密码是否正确",
					});
				});
			break;

		case LoginType.PHONE:
			authStore
				.loginByPhone(phone.value, code.value)
				.then((userInfoVO) => {
					if (userInfoVO) {
						loginSuccessHandler(userInfoVO);
					}
				})
				.catch(() => {
					loginLoading.value = false;
					notification["error"]({
						content: "登录出错",
						meta: "请检查手机号或者验证码是否正确",
					});
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
