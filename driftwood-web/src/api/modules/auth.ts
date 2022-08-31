import { AxiosResponse } from "axios";
import AxiosInstance from "@/api/axios";

/**
 * 登录参数
 */
interface LoginParam {
	/**
	 * OAuth2授权类型
	 */
	grant_type: string;

	/**
	 * 设备类型
	 */
	client_type: number;
}

/**
 * 密码登录参数
 */
interface LoginParamPassword extends LoginParam {
	username: string;
	password: string;
}

/**
 * 短信登录参数
 */
interface LoginParamPhone extends LoginParam {
	phone: string;
	code: string;
}

/**
 * 登录成功返回的token信息
 */
interface TokenInfo {
	access_token: string;
	refresh_token: string;
	token_type: string;
	expires_in: number;
}

type CreateService = (path: string) => {
	/**
	 * 密码登录
	 */
	loginByPassword: (
		loginParam: LoginParamPassword
	) => Promise<AxiosResponse<TokenInfo>>;

	/**
	 * 短信登录
	 */
	loginByPhone: (
		loginParam: LoginParamPhone
	) => Promise<AxiosResponse<TokenInfo>>;
};

const createService: CreateService = (path: string) => {
	return {
		loginByPassword(loginParam: LoginParamPassword) {
			return AxiosInstance.post<TokenInfo>(path, null, {
				headers: {
					Authorization: "Basic aWRfcGFzc3dvcmQ6c2VjcmV0",
				},
				params: loginParam,
			});
		},
		loginByPhone(loginParam: LoginParamPhone) {
			return AxiosInstance.post<TokenInfo>(path, null, {
				headers: {
					Authorization: "Basic aWRfcGhvbmU6c2VjcmV0",
				},
				params: loginParam,
			});
		},
	};
};

const authService = createService("/auth/oauth2/token");

export { authService };
export type { TokenInfo, LoginParamPassword, LoginParamPhone };
