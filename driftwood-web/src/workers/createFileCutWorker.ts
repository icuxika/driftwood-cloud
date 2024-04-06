import createChunk from "@/workers/createChuck";

/**
 * 当接收到消息时的处理函数。
 * @param e 事件对象，包含了文件信息和处理参数。
 * - file: 需要处理的文件。
 * - CHUNK_SIZE: 每个数据块的大小。
 * - startChunkIndex: 开始处理的数据块索引。
 * - endChunkIndex: 结束处理的数据块索引。
 *
 * 函数将文件按照指定大小分割成多个数据块，并对每个数据块进行处理（如计算hash值），
 * 最后将所有处理后的数据块发送回去。
 */
onmessage = async (e) => {
    const {
        file,
        CHUNK_SIZE,
        startChunkIndex: start,
        endChunkIndex: end,
    } = e.data;

    // 创建一个存储Promise结果的数组
    const ps: Promise<{
        start: number;
        end: number;
        index: number;
        hash: string;
        blob: Blob;
    }>[] = [];

    // 为每个数据块创建一个处理Promise，并将其添加到数组中
    for (let i = start; i < end; i++) {
        ps.push(createChunk(file, i, CHUNK_SIZE));
    }

    // 等待所有数据块的处理完成
    const chunks = await Promise.all(ps);

    // 将所有处理后的数据块发送回调用方
    postMessage(chunks);
};
