import { AxiosResponse } from "axios";
import AxiosInstance from "@/api/axios";
import { ApiData } from "@/api";

/**
 * 设备类型html
 */
const CLIENT_TYPE_HTML = 0;

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
 * 登录参数
 */
interface LoginParam {
	/**
	 * 授权类型
	 */
	grantType: AuthorizationGrantType.PASSWORD | AuthorizationGrantType.PHONE;

	/**
	 * 账户标识符（用户名、手机号等）
	 */
	identifier: string;

	/**
	 * 账户登录凭证（密码、短信验证码等）
	 */
	credentials: string;

	/**
	 * 客户端类型
	 */
	clientType: number;
}

/**
 * 登录成功返回的token信息
 */
interface TokenInfo {
	accessToken: string;
	refreshToken: string;
	tokenType: string;
	expiresIn: number;
}

type CreateService = (path: string) => {
	login: (
		loginParam: LoginParam
	) => Promise<AxiosResponse<ApiData<TokenInfo>>>;
};

const createService: CreateService = (path: string) => {
	return {
		login(loginParam: LoginParam) {
			return AxiosInstance.post<ApiData<TokenInfo>>(
				path + "/login",
				loginParam
			);
		},
	};
};

const adminAuthService = createService("/admin/auth");

export {
	adminAuthService,
	CLIENT_TYPE_HTML,
	AuthorizationGrantType,
	LoginParam,
	TokenInfo,
};
