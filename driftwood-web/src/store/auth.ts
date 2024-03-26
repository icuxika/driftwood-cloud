import { defineStore } from "pinia";
import {
    adminAuthService,
    AuthorizationGrantType,
    CLIENT_TYPE_HTML,
    TokenInfo,
} from "@/api/modules/admin/auth";
import { resolveAxiosResult } from "@/api";
import { userService, UserInfoVO } from "@/api/modules/user/user";

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
        async loginByPassword(
            username: string,
            password: string
        ): Promise<UserInfoVO | null> {
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
                    // Action 使用箭头函数将会无法获取this，提示TS2532: Object is possibly 'undefined'.
                    this.setTokenInfo(tokenInfo);
                    return await resolveAxiosResult(userService.getUserInfo);
                }
            } catch (error) {
                return Promise.reject(error);
            }
            return null;
        },

        /**
         * 短信模式登录
         */
        async loginByPhone(
            phone: string,
            code: string
        ): Promise<UserInfoVO | null> {
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
                    this.setTokenInfo(tokenInfo);
                    return await resolveAxiosResult(userService.getUserInfo);
                }
            } catch (error) {
                return Promise.reject(error);
            }
            return null;
        },
    },
});
