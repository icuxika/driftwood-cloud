import { AxiosResponse } from "axios";
import AxiosInstance from "@/api/axios";
import { ApiData } from "@/api";

type ChunkUploadUrlMap = {
	[key: string]: string;
};

type CreateService = (path: string) => {
	/**
	 * 大文件分片上传-创建分片上传请求
	 */
	createMultipartUpload: (
		fileName: string,
		chunkNumber: number
	) => Promise<AxiosResponse<ApiData<ChunkUploadUrlMap>>>;

	/**
	 * 大文件分片上传-合并上传完成的分片文件
	 * @param fileName
	 * @param uploadId
	 */
	completeMultipartUpload: (
		fileName: string,
		uploadId: string
	) => Promise<AxiosResponse<ApiData<never>>>;
};

const createService: CreateService = (path: string) => {
	return {
		createMultipartUpload(fileName: string, chunkNumber: number) {
			return AxiosInstance.get(
				path +
					"/createMultipartUpload" +
					"?fileName=" +
					fileName +
					"&chunkNumber=" +
					chunkNumber
			);
		},

		completeMultipartUpload(fileName: string, uploadId: string) {
			return AxiosInstance.get(
				path +
					"/completeMultipartUpload" +
					"?fileName=" +
					fileName +
					"&uploadId=" +
					uploadId
			);
		},
	};
};

const fileService = createService("/file/file");

export { fileService };
export type { ChunkUploadUrlMap };
