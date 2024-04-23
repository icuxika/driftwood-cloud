import router from "@/router/index";
import axios, { AxiosResponse } from "axios";
import {
    AuthorizationGrantType,
    CLIENT_TYPE_HTML,
    adminAuthService,
} from "./modules/admin/auth";

const instance = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_URL_PLACEHOLDER,
    validateStatus: function (status) {
        // 此处决定请求响应status不满足该条件时进入error分支
        return status >= 200 && status < 300;
    },
    timeout: 1000 * 5, // 超时时间
    responseType: "json",
});

const randomUUID = (): string => {
    const temp = URL.createObjectURL(new Blob());
    const uuid = temp.toString();
    URL.revokeObjectURL(temp);
    return uuid.substring(uuid.lastIndexOf("/") + 1);
};

/**
 * 请求拦截器
 */
instance.interceptors.request.use(
    (config) => {
        const authorization = config.headers["Authorization"];
        if (
            typeof authorization === "undefined" &&
            config.url?.indexOf("login") == -1 &&
            config.url?.indexOf("refreshToken") == -1
        ) {
            // config.headers["Authorization"] = "Bearer " + store.state.auth.accessToken
            config.headers["Authorization"] =
                "Bearer " + localStorage.getItem("accessToken");
        }
        config.headers["Trace-Id"] = randomUUID();
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

/**
 * 响应拦截器
 */
const createResponseInterceptor = async () => {
    let isRefreshing = false;
    let requests: ((accessToken: string) => void)[] = [];

    instance.interceptors.response.use(
        (response: AxiosResponse) => {
            return response;
        },
        async (error) => {
            const response = error.response;
            if (response) {
                switch (response.status) {
                    case 401: {
                        if (!isRefreshing) {
                            isRefreshing = true;
                            try {
                                const newAccessToken =
                                    await refreshAccessToken();
                                requests.forEach((f) => f(newAccessToken));
                                requests = [];
                                response.config.headers["Authorization"] =
                                    "Bearer " + newAccessToken;
                                return instance(response.config);
                            } catch (error) {
                                console.log(error);
                                await toLogin();
                            } finally {
                                isRefreshing = false;
                            }
                        } else {
                            return new Promise((resolve) => {
                                requests.push((accessToken) => {
                                    response.config.headers["Authorization"] =
                                        "Bearer " + accessToken;
                                    resolve(instance(response.config));
                                });
                            });
                        }
                        break;
                    }
                    case 403: {
                        window.$message.warning("未授权");
                        break;
                    }
                    case 404: {
                        window.$message.error("404");
                        break;
                    }
                    default: {
                        window.$message.error("未知错误");
                    }
                }
            } else {
                // 断网、连接超时
                window.$message.error("请检查服务器连接是否正常");
            }
            return Promise.reject(error);
        }
    );
};

/**
 * 跳转登录页面
 */
const toLogin = async () => {
    window.$message.warning("登录失效，请重新登录");
    // 跳转登录页面
    localStorage.setItem("accessToken", "");
    const currentPath = router.currentRoute.value.fullPath;
    let newDirect = currentPath;
    // 移除被跳转的路径携带的query参数，防止重复
    const existQueryIndex = currentPath.indexOf("?");
    if (existQueryIndex != -1) {
        newDirect = currentPath.slice(0, existQueryIndex);
    }
    if (currentPath.indexOf("/login") == -1) {
        router.push({
            path: "/login",
            replace: true,
            query: {
                redirect: newDirect,
            },
        });
    }
};

/**
 * 使用refreshToken刷新accessToken
 * @returns 新的accessToken
 */
async function refreshAccessToken(): Promise<string> {
    const localRefreshToken = localStorage.getItem("refreshToken");
    if (!localRefreshToken) {
        return Promise.reject("localStorage没有缓存refreshToken");
    }
    const tokenInfoResponse = await adminAuthService.refreshToken({
        loginGrantType: AuthorizationGrantType.PASSWORD,
        grantType: "refresh_token",
        refreshToken: localRefreshToken,
        clientType: CLIENT_TYPE_HTML,
    });
    const tokenInfoApiData = tokenInfoResponse.data;
    if (tokenInfoApiData.success) {
        const tokenInfo = tokenInfoApiData.data;
        if (tokenInfo) {
            localStorage.setItem("accessToken", tokenInfo.accessToken);
            localStorage.setItem("refreshToken", tokenInfo.refreshToken);
            return tokenInfo.accessToken;
        }
        return Promise.reject("不应出现的数据为空错误");
    }
    return Promise.reject(tokenInfoApiData.msg);
}

createResponseInterceptor();

export default instance;
