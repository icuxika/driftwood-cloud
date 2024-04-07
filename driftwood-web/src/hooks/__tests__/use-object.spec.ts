import { useObject } from "@/hooks/use-object";
import { describe, expect, test } from "vitest";
const { getPropertyValue } = useObject();

describe("useObject", () => {
    test("getPropertyValue", () => {
        const person = {
            name: "张三",
            age: 18,
        };
        Object.keys(person).forEach((key) => {
            expect(getPropertyValue(person, key)).toBe(
                person[key as keyof typeof person]
            );
        });
    });
});
