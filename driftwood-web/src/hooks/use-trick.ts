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

    /**
     * 依次顺序执行一系列任务
     * 所有任务完成后可以得到每个任务的结果
     * start用于启动任务，pause用于暂停任务
     * 经过暂停再运行后，第一次执行暂停时返回的Promise会一直阻塞
     * @param tasks 任务列表，每个任务无参、异步
     */
    const processPauseableTasks = <T>(tasks: (() => Promise<T>)[]) => {
        let isRunning = false;
        let i = 0;
        const result: T[] = [];
        let prom: Promise<T[]>;
        return {
            start() {
                // eslint-disable-next-line no-async-promise-executor
                return new Promise<T[]>(async (resolve, reject) => {
                    if (prom) {
                        prom.then(resolve, reject);
                        return;
                    }
                    if (isRunning) {
                        return;
                    }
                    isRunning = true;
                    while (i < tasks.length) {
                        try {
                            console.log(i, "执行中");
                            result.push(await tasks[i]());
                            console.log(i, "执行完成");
                        } catch (error) {
                            isRunning = false;
                            reject(error);
                            prom = Promise.reject(error);
                            return;
                        }
                        i++;
                        if (!isRunning && i < tasks.length - 1) {
                            console.log(i, "执行被中断");
                            return;
                        }
                    }
                    isRunning = false;
                    resolve(result);
                    prom = Promise.resolve(result);
                });
            },
            pause() {
                isRunning = false;
            },
        };
    };

    return {
        sleep,
        debounce0,
        debounce,
        componentRef,
        debounceRef,
        processPauseableTasks,
    };
};
