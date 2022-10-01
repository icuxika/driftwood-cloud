import { AxiosResponse } from "axios";
import AxiosInstance from "@/api/axios";
import { ApiData } from "@/api";

type CreateService = (path: string) => {
	uploadFile: (
		file: File,
		progress: (percent: number) => void
	) => Promise<AxiosResponse<ApiData<number>>>;

	downloadFile: (fileId: number) => Promise<AxiosResponse>;
};

const createService: CreateService = (path: string) => {
	return {
		uploadFile(file, progress) {
			const formData = new FormData();
			formData.append("file", file, file.name);
			return AxiosInstance.post(path + "/uploadFile", formData, {
				headers: {
					"Content-Type": "multipart/form-data",
				},
				onUploadProgress: function (progressEvent: ProgressEvent) {
					progress(
						(progressEvent.loaded / progressEvent.total) * 100
					);
				},
			});
		},

		downloadFile(fileId) {
			return AxiosInstance.get(path + "/" + fileId);
		},
	};
};

const fileService = createService("/admin/file");

export { fileService };
