import SparkMD5 from "spark-md5";

/**
 * 创建并返回一个数据块的Promise对象。
 * 该函数将大文件分割成小块（chunk），并对每个块计算MD5哈希值。
 *
 * @param file 要处理的Blob文件对象。
 * @param index 当前处理的数据块的索引。
 * @param chunkSize 每个数据块的大小。
 * @returns 返回一个Promise，该Promise解析为一个对象，包含数据块的信息：
 *          - start: 数据块开始的字节位置。
 *          - end: 数据块结束的字节位置（不包含）。
 *          - index: 数据块的索引。
 *          - hash: 数据块的MD5哈希值。
 *          - blob: 数据块的Blob对象。
 */
export default function createChunk(
    file: Blob,
    index: number,
    chunkSize: number
): Promise<{
    start: number;
    end: number;
    index: number;
    hash: string;
    blob: Blob;
}> {
    return new Promise((resolve) => {
        // 计算当前块的开始和结束字节位置
        const start = index * chunkSize;
        const end = Math.min(start + chunkSize, file.size);

        // 初始化MD5计算器
        const spark = new SparkMD5.ArrayBuffer();
        const fileReader = new FileReader();
        const blob = file.slice(start, end);

        fileReader.onload = (e) => {
            if (e.target) {
                // 将读取的结果（ArrayBuffer）附加到MD5计算器
                spark.append(e.target.result as ArrayBuffer);
                // 计算完成，解析Promise
                resolve({
                    start,
                    end,
                    index,
                    hash: spark.end(),
                    blob,
                });
            }
        };
        fileReader.readAsArrayBuffer(blob);
    });
}
