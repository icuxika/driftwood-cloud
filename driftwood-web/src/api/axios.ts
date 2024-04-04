import router from "@/router/index";
import axios, { AxiosRequestConfig, AxiosResponse } from "axios";
import {
    AuthorizationGrantType,
    CLIENT_TYPE_HTML,
    adminAuthService,
} from "./modules/admin/auth";

const instance = axios.create({
    // baseURL: import.meta.env.VITE_APP_BASE_URL,
    baseURL: "/api",
    validateStatus: function (status) {
        // 此处决定请求响应status不满足该条件时进入error分支
        return status >= 200 && status < 300;
    },
    timeout: 1000 * 5, // 超时时间
    responseType: "json",
});

let requestInExecutionList: string[] = [];

/**
 * 是否过滤重复请求
 */
const stopRepeatRequest = (
    requestList: string[],
    config: AxiosRequestConfig,
    message: string
) => {
    const url = [
        config.method,
        config.url,
        JSON.stringify(config.params),
        JSON.stringify(config.data),
    ].join("&");
    if (requestList.length) {
        const array = requestList.filter((item) => item === url);
        if (array.length) {
            console.log(message);
            return true;
        }
    }
    requestInExecutionList.push(url);
    return false;
};

/**
 * 解除对请求的重复验证
 */
const allowRequest = (requestList: string[], config: AxiosRequestConfig) => {
    const url = [
        config.method,
        config.url,
        JSON.stringify(config.params),
        JSON.stringify(config.data),
    ].join("&");
    if (requestList.length) {
        requestInExecutionList = requestList.filter((item) => item !== url);
    }
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
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

/**
 * 响应拦截器
 */
instance.interceptors.response.use(
    (response: AxiosResponse) => {
        return response;
    },
    async (error) => {
        const response = error.response;
        if (response) {
            switch (response.status) {
                case 401: {
                    const localRefreshToken =
                        localStorage.getItem("refreshToken");

                    const isRefreshToken =
                        (response.config.headers[
                            "__isRefreshToken"
                        ] as boolean) || false;

                    if (localRefreshToken) {
                        // localStorage中有token的缓存
                        if (isRefreshToken) {
                            // 判断是不是刷新token的请求，防止递归
                            // 直接转登录页面
                        } else {
                            // 尝试手动刷新token
                            try {
                                const tokenInfoResponse =
                                    await adminAuthService.refreshToken({
                                        loginGrantType:
                                            AuthorizationGrantType.PASSWORD,
                                        grantType: "refresh_token",
                                        refreshToken: localRefreshToken,
                                        clientType: CLIENT_TYPE_HTML,
                                    });
                                const tokenInfoApiData = tokenInfoResponse.data;
                                if (tokenInfoApiData.success) {
                                    console.log("刷新token成功");
                                    const tokenInfo = tokenInfoApiData.data;
                                    if (tokenInfo) {
                                        localStorage.setItem(
                                            "accessToken",
                                            tokenInfo.accessToken
                                        );
                                        localStorage.setItem(
                                            "refreshToken",
                                            tokenInfo.refreshToken
                                        );
                                        // 重新请求失败的请求
                                        response.config.headers[
                                            "Authorization"
                                        ] = "Bearer " + tokenInfo.accessToken;
                                        return instance(response.config);
                                    }
                                }
                            } catch (error) {
                                console.log("尝试刷新token出错", error);
                            }
                        }
                    } else {
                        // 未登录过，直接转登录页面
                    }

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

export default instance;
