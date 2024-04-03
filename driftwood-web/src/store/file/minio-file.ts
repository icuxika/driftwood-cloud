import { defineStore } from "pinia";
import {
    ChunkUploadUrlMap,
    minioFileService,
} from "@/api/modules/minio/minio-file";
import { resolveAxiosResult } from "@/api";

interface MinioFileState {}

export const useMinioFileStore = defineStore("minioFile", {
    state: (): MinioFileState => ({}),
    getters: {},
    actions: {
        async createMultipartUpload(
            fileName: string,
            chunkNumber: number
        ): Promise<ChunkUploadUrlMap | null> {
            return resolveAxiosResult(() =>
                minioFileService.createMultipartUpload(fileName, chunkNumber)
            );
        },

        async completeMultipartUpload(
            fileName: string,
            uploadId: string
        ): Promise<null> {
            return resolveAxiosResult(() =>
                minioFileService.completeMultipartUpload(fileName, uploadId)
            );
        },
    },
});
