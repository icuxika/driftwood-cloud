import { useTrick } from "@/hooks/use-trick";
import { mount } from "@vue/test-utils";
import { describe, expect, test } from "vitest";
import { defineComponent } from "vue";

const { debounce0, debounce, sleep, componentRef } = useTrick();

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
        // eslint-disable-next-line vue/one-component-per-file
        const DemoComponent = defineComponent({
            setup(props, { expose }) {
                const title = "demo";
                expose({
                    title,
                });
            },
            template: "<div></div>",
        });

        // eslint-disable-next-line vue/one-component-per-file
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
});
