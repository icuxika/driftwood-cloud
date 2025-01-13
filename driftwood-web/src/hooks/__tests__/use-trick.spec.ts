import { useTrick } from "@/hooks/use-trick";
import { mount } from "@vue/test-utils";
import { describe, expect, test } from "vitest";
import { defineComponent } from "vue";

const { debounce0, debounce, sleep, componentRef, processPauseableTasks } =
    useTrick();

describe("useTrick", () => {
    test("debounce", { timeout: 10 * 1000 }, async () => {
        const target = (a: number, b: number, c: number) => {
            console.log(a, b, c);
        };
        const f = debounce0(target, 1000, 1, 2);
        f(3, 4);
        f(3, 4);
        f(3, 4);
        await sleep(2000);
        f();
        await sleep(1000);

        const f1 = debounce(target);
        f1(1, 2, 3);
        await sleep(2000);
    });

    test("componentRef", async () => {
        const DemoComponent = defineComponent({
            setup(props, { expose }) {
                const title = "demo";
                expose({
                    title,
                });
            },
            template: "<div></div>",
        });

        const TestComponent = defineComponent({
            components: {
                DemoComponent,
            },
            setup() {
                const demoRef = componentRef(DemoComponent);
            },
            template: "<DemoComponent ref='demoRef'/>",
        });

        const wrapper = mount(TestComponent, {
            attachTo: document.body,
        });
        expect((wrapper.vm.$refs.demoRef as any).title).toBe("demo");
    });

    test("processPauseableTasks", { timeout: 30 * 1000 }, async () => {
        const tasks: (() => Promise<number>)[] = [];
        for (let i = 0; i < 5; i++) {
            tasks.push(
                () =>
                    new Promise((resolve) => {
                        setTimeout(() => {
                            resolve(i);
                        }, 2000);
                    })
            );
        }
        const processor = processPauseableTasks<number>(tasks);
        setTimeout(() => {
            console.log("pause");
            processor.pause();
        }, 2000);
        setTimeout(async () => {
            console.log("restart");
            const results = await processor.start();
            console.log(2, results);
        }, 4000);
        // 经过暂停再运行后，第一次执行暂停时返回的Promise会一直阻塞
        const results = await processor.start();
        console.log(1, results);
    });
});
