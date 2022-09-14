import { AxiosResponse } from "axios";
import { ApiData } from "@/api/index";

const isMockMode = import.meta.env.MODE === "mock";

const mockResponse = <T>(mockData: T) => {
	return Promise.resolve<AxiosResponse<ApiData<T>>>({
		data: {
			data: mockData,
			code: 10000,
			msg: "操作成功",
			success: true,
		},
		status: 200,
		statusText: "",
		headers: null,
		config: {},
	});
};

export { isMockMode, mockResponse };
