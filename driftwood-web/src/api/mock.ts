import { ApiData } from "@/api/index";
import { AxiosResponse } from "axios";

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
		headers: {},
		config: {} as any,
	});
};

export { isMockMode, mockResponse };
