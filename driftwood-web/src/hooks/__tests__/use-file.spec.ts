import { describe, expect, test } from "vitest";
import { useFile } from "../use-file";
const { cutFile } = useFile();

describe("Banner", () => {
    test("equals", { timeout: 600 * 1000 }, async () => {
        const arrayBuffer = await fetch(
            "https://dldir1.qq.com/qqfile/qq/QQNT/Windows/QQ_9.9.7_240305_x64_01.exe",
            { method: "GET" }
        ).then((response) => response.arrayBuffer());
        const file = new File([arrayBuffer], "qq.exe", {
            lastModified: new Date().getTime(),
        });
        const fileSize = file.size;
        console.log("fileSize: ", fileSize);
        console.log("arrayBuffer length: ", arrayBuffer.byteLength);
        expect(fileSize).toBe(arrayBuffer.byteLength);

        // 一直超时，暂时无法解决
        const result = await cutFile(file);
        const resultSize = result.reduce<number>((pre, cur) => {
            pre += cur.blob.size;
            return pre;
        }, 0);
        expect(fileSize).toBe(resultSize);
    });
});
