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

interface FileUploadPart {
    part: Blob;
    objectName: string;
    uploadId: string;
    partNumber: number;
    partSize: number;
    fileOffset: number;
    md5Digest: string;
}

interface InitiateMultipartUploadResult {
    bucketName: string;
    key: string;
    uploadId: string;
}

interface UploadPartResult {
    partNumber: number;
    etag: string;
}

interface PartETagDTO {
    partNumber: number;
    tag: string;
}

interface CompleteMultipartUploadRequestDTO {
    objectName: string;
    uploadId: string;
    partETags: PartETagDTO[];
}

interface CompleteMultipartUploadResult {
    key: string;
    location: string;
}

type CreateService = (path: string) => {
    uploadFile: (
        file: File,
        progress: (percent: number) => void
    ) => ApiDataResponse<AdminFileVO>;

    downloadFile: (
        fileId: number,
        progress: (percent: number) => void
    ) => Promise<AxiosResponse>;

    getFilePath: (fileId: number) => ApiDataResponse<FileVO>;

    initiateMultipartUpload: (
        fileName: string
    ) => ApiDataResponse<InitiateMultipartUploadResult>;

    uploadPart: (
        part: FileUploadPart,
        progress: (percent: number) => void
    ) => ApiDataResponse<UploadPartResult>;

    completeMultipartUpload: (
        upload: CompleteMultipartUploadRequestDTO
    ) => ApiDataResponse<CompleteMultipartUploadResult>;
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
                timeout: 1000 * 50,
                onUploadProgress: function (progressEvent: AxiosProgressEvent) {
                    if (progressEvent.total) {
                        progress(
                            (progressEvent.loaded / progressEvent.total) * 100
                        );
                    }
                },
            });
        },

        downloadFile(fileId, progress) {
            return AxiosInstance.get(`${path}/${fileId}`, {
                responseType: "blob",
                timeout: 1000 * 50,
                onDownloadProgress: function (
                    progressEvent: AxiosProgressEvent
                ) {
                    if (progressEvent.total) {
                        progress(
                            (progressEvent.loaded / progressEvent.total) * 100
                        );
                    }
                },
            });
        },

        getFilePath(fileId) {
            return AxiosInstance.get(`${path}/getFilePath/${fileId}`);
        },

        initiateMultipartUpload(fileName) {
            const formData = new FormData();
            formData.append("fileName", fileName);
            return AxiosInstance.post(
                `${path}/initiateMultipartUpload`,
                formData,
                {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                }
            );
        },

        uploadPart(part, progress) {
            const formData = new FormData();
            formData.append("file", part.part, part.objectName);
            formData.append("objectName", part.objectName);
            formData.append("uploadId", part.uploadId);
            formData.append("partNumber", part.partNumber.toString());
            formData.append("partSize", part.partSize.toString());
            formData.append("fileOffset", part.fileOffset.toString());
            formData.append("md5Digest", part.md5Digest);
            return AxiosInstance.post(`${path}/uploadPart`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
                timeout: 1000 * 60,
                onUploadProgress: function (progressEvent: AxiosProgressEvent) {
                    if (progressEvent.total) {
                        progress(
                            (progressEvent.loaded / progressEvent.total) * 100
                        );
                    }
                },
            });
        },

        completeMultipartUpload(upload: CompleteMultipartUploadRequestDTO) {
            return AxiosInstance.post(
                `${path}/completeMultipartUpload`,
                upload
            );
        },
    };
};

const fileService = createService("/admin/file");

export { fileService };

export type { UploadPartResult };
