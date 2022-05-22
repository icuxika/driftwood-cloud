import axios, { AxiosRequestConfig, AxiosResponse } from "axios";
import router from "@/router/index";

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
	(config: AxiosRequestConfig) => {
		const authorization = config.headers["Authorization"];
		if (
			typeof authorization === "undefined" &&
			config.url?.indexOf("login") == -1
		) {
			// config.headers["Authorization"] = "Bearer " + store.state.auth.accessToken
			config.headers["Authorization"] =
				"Bearer " + localStorage.getItem("accessToken");
		}
		const repeatFlag = stopRepeatRequest(
			requestInExecutionList,
			config,
			`${config.url} 请求重复`
		);
		if (repeatFlag) {
			return {
				cancelToken: new axios.CancelToken((cancel) =>
					cancel(JSON.stringify(config))
				),
			};
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
		allowRequest(requestInExecutionList, response.config);
		return response;
	},
	(error) => {
		if (axios.isCancel(error)) {
			const config = JSON.parse(error.message);
			allowRequest(requestInExecutionList, config);
		} else {
			allowRequest(requestInExecutionList, error.config);
		}
		const response = error.response;
		if (response) {
			switch (response.status) {
				case 401: {
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
							path: "login",
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
