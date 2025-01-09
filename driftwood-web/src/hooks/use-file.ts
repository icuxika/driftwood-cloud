import FileCutWorker from "@/workers/createFileCutWorker?worker";
import { AxiosResponse } from "axios";

export const useFile = () => {
    const getFilename = (contentDisposition: string) => {
        if (
            contentDisposition.includes("filename") ||
            contentDisposition.includes("filename*")
        ) {
            if (contentDisposition.includes("filename*")) {
                return decodeURIComponent(
                    contentDisposition.split("filename*=")[1].split("''")[1]
                );
            } else {
                return decodeURIComponent(
                    contentDisposition.split("filename=")[1].replaceAll('"', "")
                );
            }
        } else {
            return "undefined";
        }
    };

    const downloadFile = (response: AxiosResponse) => {
        if (response.data && response.data.size === 0) {
            window.$message.error("下载出错，请重试！");
            return;
        }
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement("a");
        link.href = url;
        link.download = getFilename(response.headers["content-disposition"]);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    };

    const downloadFileByUrl = async (url: string, name: string) => {
        const link = document.createElement("a");
        link.href = url;
        link.download = name;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    // 定义切片大小为5MB
    const CHUNK_SIZE = 1024 * 1024 * 5; // 5MB
    // 获取系统硬件并发数或默认设置为4
    const THREAD_COUNT = navigator.hardwareConcurrency || 4;
    /**
     * 将文件切割成多个片段，并通过Web Workers计算每个片段的哈希值。
     * @param file 需要切割和计算哈希的文件。
     * @returns 返回一个Promise，该Promise解析为一个对象数组，每个对象包含片段的起始位置、结束位置、索引、哈希值和Blob对象。
     */
    const cutFile = (
        file: File
    ): Promise<
        {
            start: number;
            end: number;
            index: number;
            hash: string;
            blob: Blob;
        }[]
    > => {
        return new Promise((resolve) => {
            // 存储每个片段的信息
            const result: {
                start: number;
                end: number;
                index: number;
                hash: string;
                blob: Blob;
            }[] = [];
            // 计算总共需要多少个片段
            const chunkCount = Math.ceil(file.size / CHUNK_SIZE);
            // 根据线程数计算每个线程处理的片段数量
            const threadChunkCount = Math.ceil(chunkCount / THREAD_COUNT);
            // 记录已完成的线程数
            let finishCount = 0;
            // 创建并启动处理线程
            for (let i = 0; i < THREAD_COUNT; i++) {
                const worker = new FileCutWorker();
                // 计算当前线程处理的起始和结束片段索引
                const start = i * threadChunkCount;
                let end = (i + 1) * threadChunkCount;
                // 如果计算超出了总片段数，则调整结束索引
                if (end > chunkCount) {
                    end = chunkCount;
                }
                // 向工作线程发送任务
                worker.postMessage({
                    file,
                    CHUNK_SIZE,
                    startChunkIndex: start,
                    endChunkIndex: end,
                });
                // 处理线程返回结果时的回调
                worker.onmessage = (e) => {
                    for (let i = start; i < end; i++) {
                        result[i] = e.data[i - start];

                        if (i === chunkCount - 1) {
                            result[i].end = file.size;
                        }
                    }
                    worker.terminate();
                    finishCount++;
                    if (finishCount == THREAD_COUNT) {
                        resolve(result);
                    }
                };
                worker.onerror = (e) => {
                    console.log(e);
                    worker.terminate();
                };
            }
        });
    };

    return {
        downloadFile,
        downloadFileByUrl,
        cutFile,
    };
};
