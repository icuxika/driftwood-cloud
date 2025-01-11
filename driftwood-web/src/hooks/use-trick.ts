import { customRef, ref } from "vue";

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
    const debounce0 = <T extends (...args: any[]) => any>(
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
     * 函数防抖，返回的函数的参数类型与fn一致
     * @param fn 需要进行防抖的目标函数
     * @param delay 延迟时间，默认1000ms
     */
    const debounce = <T extends any[], R>(
        fn: (...args: T) => R,
        delay = 1000
    ): ((...args: T) => void) => {
        let timer: NodeJS.Timeout;
        return (...args: T) => {
            clearTimeout(timer);
            timer = setTimeout(function () {
                fn(...args);
            }, delay);
        };
    };

    /**
     * ref Vue组件时自动获取其类型
     */
    const componentRef = <T extends abstract new (...args: any[]) => any>(
        _component: T
    ) => ref<InstanceType<T>>();

    /**
     * 防抖 Ref
     */
    const debounceRef = <T>(target: T, delay: number) => {
        let timer: NodeJS.Timeout;
        return customRef<T>((track, trigger) => ({
            get() {
                track();
                return target;
            },
            set(value) {
                clearTimeout(timer);
                timer = setTimeout(() => {
                    target = value;
                    trigger();
                }, delay);
            },
        }));
    };

    return {
        sleep,
        debounce0,
        debounce,
        componentRef,
        debounceRef,
    };
};
