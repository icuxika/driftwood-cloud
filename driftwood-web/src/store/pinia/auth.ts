import { defineStore } from "pinia";
import {
	authService,
	LoginParamPassword,
	LoginParamPhone,
	TokenInfo,
} from "@/api/modules/auth";

/**
 * 授权模式
 */
const enum AuthorizationGrantType {
	/**
	 * 授权码模式
	 */
	AUTHORIZATION_CODE = "authorization_code",

	/**
	 * 密码模式
	 */
	PASSWORD = "password",

	/**
	 * 短信模式
	 */
	PHONE = "phone",
}

/**
 * 设备类型html
 */
const CLIENT_TYPE_HTML = 0;

interface AuthState {
	/**
	 * 请求凭证
	 */
	accessToken: string;

	/**
	 * 用来刷新请求凭证的凭证
	 */
	refreshToken: string;

	/**
	 * 过期时间（单位：秒）
	 */
	expiresIn: number;
}

export const useAuthStore = defineStore("auth", {
	state: (): AuthState => ({
		accessToken: "",
		refreshToken: "",
		expiresIn: 0,
	}),
	getters: {},
	actions: {
		setTokenInfo(tokenInfo: TokenInfo) {
			this.accessToken = tokenInfo.access_token;
			this.refreshToken = tokenInfo.refresh_token;
			this.expiresIn = tokenInfo.expires_in;

			localStorage.setItem("accessToken", tokenInfo.access_token);
		},

		/**
		 * 密码模式登录
		 */
		async loginByPassword(password: LoginParamPassword) {
			return new Promise((resolve, reject) => {
				authService
					.loginByPassword({
						grant_type: AuthorizationGrantType.PASSWORD,
						client_type: CLIENT_TYPE_HTML,
						username: password.username,
						password: password.password,
					})
					.then((response) => {
						const tokenInfo = response.data;
						this.setTokenInfo(tokenInfo);
						resolve(0);
					})
					.catch((error) => {
						reject(error);
					});
			});
		},

		/**
		 * 短信模式登录
		 */
		async loginByPhone(phone: LoginParamPhone) {
			return new Promise((resolve, reject) => {
				authService
					.loginByPhone({
						grant_type: AuthorizationGrantType.PHONE,
						client_type: CLIENT_TYPE_HTML,
						phone: phone.phone,
						code: phone.code,
					})
					.then((response) => {
						const tokenInfo = response.data;
						this.setTokenInfo(tokenInfo);
						resolve(0);
					})
					.catch((error) => {
						reject(error);
					});
			});
		},
	},
});
