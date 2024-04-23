import { ApiDataResponse } from "@/api";
import AxiosInstance from "@/api/axios";
import { AxiosProgressEvent, AxiosResponse } from "axios";

interface AdminFileVO {
    id: number;
    filepath: string;
}

interface FileVO {
    id: number;
    filepath: string;
    originalFilename: string;
}

type CreateService = (path: string) => {
    uploadFile: (
        file: File,
        progress: (percent: number) => void
    ) => ApiDataResponse<AdminFileVO>;

    downloadFile: (fileId: number) => Promise<AxiosResponse>;

    getFilePath: (fileId: number) => ApiDataResponse<FileVO>;
};

const createService: CreateService = (path: string) => {
    return {
        uploadFile(file, progress) {
            const formData = new FormData();
            formData.append("file", file, file.name);
            return AxiosInstance.post(`${path}/uploadFile`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
                onUploadProgress: function (progressEvent: AxiosProgressEvent) {
                    if (progressEvent.total) {
                        progress(
                            (progressEvent.loaded / progressEvent.total) * 100
                        );
                    }
                },
            });
        },

        downloadFile(fileId) {
            return AxiosInstance.get(`${path}/${fileId}`, {
                responseType: "blob",
            });
        },

        getFilePath(fileId) {
            return AxiosInstance.get(`${path}/getFilePath/${fileId}`);
        },
    };
};

const fileService = createService("/admin/file");

export { fileService };
