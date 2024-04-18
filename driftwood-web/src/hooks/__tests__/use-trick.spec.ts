import { useTrick } from "@/hooks/use-trick";
import { mount } from "@vue/test-utils";
import { describe, test } from "vitest";
import { defineComponent } from "vue";

const { debounce, sleep, componentRef } = useTrick();

describe("useTrick", () => {
    test("debounce", async () => {
        const f = debounce(
            function (a, b, c, d) {
                console.log(a, b, c, d);
            },
            2000,
            1,
            2
        );
        f(3, 4);
        f(3, 4);
        f(3, 4);
        f();
        await sleep(4000);
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
        console.log(wrapper.vm.$refs.demoRef);
    });
});
