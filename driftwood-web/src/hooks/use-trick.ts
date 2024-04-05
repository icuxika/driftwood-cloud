export const useTrick = () => {
    /**
     * 阻塞函数
     * 使用方式：await sleep(2000);
     * @param milliseconds 毫秒
     * @returns Promise
     */
    const sleep = (milliseconds: number) =>
        new Promise((resolve) => setTimeout(resolve, milliseconds));
    return {
        sleep,
    };
};
