import { useFile } from "@/hooks/use-file";
import { describe, expect, test } from "vitest";
const { cutFile } = useFile();

describe("useFile", () => {
    test("cutFile", { timeout: 60 * 1000 }, async () => {
        const arrayBuffer = await fetch(
            "https://mirrors.tuna.tsinghua.edu.cn/github-release/cmderdev/cmder/v1.3.24/cmder_mini.zip",
            { method: "GET" }
        ).then((response) => response.arrayBuffer());
        const file = new File([arrayBuffer], "cmder_mini.zip", {
            lastModified: new Date().getTime(),
        });
        const fileSize = file.size;
        console.log("fileSize: ", fileSize);
        console.log("arrayBuffer length: ", arrayBuffer.byteLength);
        expect(fileSize).toBe(arrayBuffer.byteLength);

        // 暂时不知道vitest如何正确调用包含Web Worker逻辑的函数
        const result = await cutFile(file);
        const resultSize = result.reduce<number>((pre, cur) => {
            pre += cur.blob.size;
            return pre;
        }, 0);
        expect(fileSize).toBe(resultSize);
    });
});
