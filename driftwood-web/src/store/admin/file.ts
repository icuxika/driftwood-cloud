import { resolveAxiosResult } from "@/api";
import { fileService } from "@/api/modules/admin/file";
import { defineStore } from "pinia";

interface FileState {}

export const useFileStore = defineStore("file", {
    state: (): FileState => ({}),
    getters: {},
    actions: {
        async uploadFile(file: File, progress: (percent: number) => void) {
            return resolveAxiosResult(() =>
                fileService.uploadFile(file, progress)
            );
        },

        async downloadFile(fileId: number) {
            return fileService.downloadFile(fileId);
        },

        async getFilePath(fileId: number) {
            return resolveAxiosResult(() => fileService.getFilePath(fileId));
        },
    },
});
