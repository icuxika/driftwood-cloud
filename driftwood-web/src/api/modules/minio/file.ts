import { AxiosResponse } from "axios";
import AxiosInstance from "@/api/axios";
import { ApiData } from "@/api";

type ChunkUploadUrlMap = {
	[key: string]: string;
};

type CreateService = (path: string) => {
	createMultipartUpload: () => Promise<
		AxiosResponse<ApiData<ChunkUploadUrlMap>>
	>;

	completeMultipartUpload: () => Promise<AxiosResponse<ApiData<never>>>;
};

const createService: CreateService = (path: string) => {
	return {
		createMultipartUpload() {
			return AxiosInstance.get(path + "/createMultipartUpload");
		},

		completeMultipartUpload() {
			return AxiosInstance.get(path + "/completeMultipartUpload");
		},
	};
};

const fileService = createService("/file/file");

export { fileService };
