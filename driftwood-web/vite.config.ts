/// <reference types="vitest" />
import vue from "@vitejs/plugin-vue";
import vueJsx from "@vitejs/plugin-vue-jsx";
import * as path from "path";
import { defineConfig, loadEnv } from "vite";

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => {
    // 读取对应mode下 .env 中的环境变量
    process.env = { ...process.env, ...loadEnv(mode, process.cwd()) };
    if (command === "serve") {
        // dev,serve命令下执行
        return {
            plugins: [vue(), vueJsx({})],
            resolve: {
                alias: {
                    "@": path.resolve(__dirname, "./src"),
                },
            },
            server: {
                proxy: {
                    "/api": {
                        target: process.env.VITE_APP_BASE_URL,
                        changeOrigin: true,
                        rewrite: (path) => path.replace(/^\/api/, ""),
                    },
                },
            },
            test: {
                environment: "jsdom",
                setupFiles: ["@vitest/web-worker"],
                onConsoleLog(
                    log: string,
                    type: "stdout" | "stderr"
                ): boolean | void {
                    console.log(log);
                },
            },
        };
    } else {
        // build 命令下执行
        return {
            plugins: [vue(), vueJsx({})],
            resolve: {
                alias: {
                    "@": path.resolve(__dirname, "./src"),
                },
            },
            build: {
                outDir: "docker/dist",
            },
        };
    }
});
