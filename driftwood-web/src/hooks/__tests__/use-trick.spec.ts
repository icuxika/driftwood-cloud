import { useTrick } from "@/hooks/use-trick";
import { describe, test } from "vitest";
const { debounce, sleep } = useTrick();

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
});
