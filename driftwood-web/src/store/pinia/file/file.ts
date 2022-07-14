import { defineStore } from "pinia";
import { ChunkUploadUrlMap, fileService } from "@/api/modules/minio/file";
import { resolveAxiosResult } from "@/api";

interface FileState {}

export const useFileStore = defineStore("file", {
	state: (): FileState => ({}),
	getters: {},
	actions: {
		async createMultipartUpload(
			fileName: string,
			chunkNumber: number
		): Promise<ChunkUploadUrlMap | null> {
			return resolveAxiosResult(() =>
				fileService.createMultipartUpload(fileName, chunkNumber)
			);
		},

		async completeMultipartUpload(
			fileName: string,
			uploadId: string
		): Promise<null> {
			return resolveAxiosResult(() =>
				fileService.completeMultipartUpload(fileName, uploadId)
			);
		},
	},
});
