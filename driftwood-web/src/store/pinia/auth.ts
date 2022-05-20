import { defineStore } from "pinia";
import {
	adminAuthService,
	AuthorizationGrantType,
	CLIENT_TYPE_HTML,
	TokenInfo,
} from "@/api/modules/admin/auth";
import { resolveAxiosResult } from "@/api";
import { userService, UserVO } from "@/api/modules/user/user";

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
			this.accessToken = tokenInfo.accessToken;
			this.refreshToken = tokenInfo.refreshToken;
			this.expiresIn = tokenInfo.expiresIn;

			localStorage.setItem("accessToken", tokenInfo.accessToken);
		},

		/**
		 * 密码模式登录
		 */
		loginByPassword: async (
			username: string,
			password: string
		): Promise<UserVO | null> => {
			try {
				const tokenInfo = await resolveAxiosResult(() =>
					adminAuthService.login({
						grantType: AuthorizationGrantType.PASSWORD,
						clientType: CLIENT_TYPE_HTML,
						identifier: username,
						credentials: password,
					})
				);
				if (tokenInfo) {
					// eslint-disable-next-line @typescript-eslint/ban-ts-comment
					// @ts-ignore
					this.setTokenInfo(tokenInfo);
					return await resolveAxiosResult(userService.getUserInfo);
				}
			} catch (error) {
				return null;
			}
			return null;
		},

		/**
		 * 短信模式登录
		 */
		loginByPhone: async (
			phone: string,
			code: string
		): Promise<UserVO | null> => {
			try {
				const tokenInfo = await resolveAxiosResult(() =>
					adminAuthService.login({
						grantType: AuthorizationGrantType.PHONE,
						clientType: CLIENT_TYPE_HTML,
						identifier: phone,
						credentials: code,
					})
				);
				if (tokenInfo) {
					// eslint-disable-next-line @typescript-eslint/ban-ts-comment
					// @ts-ignore
					this.setTokenInfo(tokenInfo);
					return await resolveAxiosResult(userService.getUserInfo);
				}
			} catch (error) {
				return null;
			}
			return null;
		},
	},
});
