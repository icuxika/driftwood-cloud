import { ref } from "vue";

export const useTrick = () => {
    /**
     * 阻塞函数
     * 使用方式：await sleep(2000);
     * @param milliseconds 毫秒
     * @returns Promise
     */
    const sleep = (milliseconds: number) =>
        new Promise((resolve) => setTimeout(resolve, milliseconds));

    /**
     * 函数防抖
     * @param fn 需要进行防抖的目标函数
     * @param delay 延迟时间，默认1000ms
     */
    const debounce = <T extends (...args: any[]) => any>(
        fn: T,
        delay = 1000,
        ...args1: any[]
    ) => {
        let timer: NodeJS.Timeout;
        return (...args2: any[]) => {
            clearTimeout(timer);
            timer = setTimeout(function () {
                fn(...args1, ...args2);
            }, delay);
        };
    };

    /**
     * ref Vue组件时自动获取其类型
     */
    const componentRef = <T extends abstract new (...args: any[]) => any>(
        _component: T
    ) => ref<InstanceType<T>>();

    return {
        sleep,
        debounce,
        componentRef,
    };
};
