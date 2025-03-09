/// <reference types="vitest" />
import vue from "@vitejs/plugin-vue";
import vueJsx from "@vitejs/plugin-vue-jsx";
import * as path from "path";
import { visualizer } from "rollup-plugin-visualizer";
import { NaiveUiResolver } from "unplugin-vue-components/resolvers";
import Components from "unplugin-vue-components/vite";
import { defineConfig, loadEnv } from "vite";

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => {
    // 读取对应mode下 .env 中的环境变量
    Object.assign(process.env, loadEnv(mode, process.cwd()));
    const NaiveUiComponents = Components({
        resolvers: [NaiveUiResolver()],
    });
    if (command === "serve") {
        // dev,serve命令下执行
        return {
            plugins: [vue(), vueJsx({}), NaiveUiComponents],
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
                    "/download": {
                        target: "https://www.aprillie.com/download/",
                        changeOrigin: true,
                        rewrite: (path) => path.replace(/^\/download/, ""),
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
                    // console.log(log);
                },
            },
        };
    } else {
        // build 命令下执行
        return {
            base: "/driftwood-web",
            plugins: [
                vue(),
                vueJsx({}),
                NaiveUiComponents,
                visualizer({
                    open: false,
                    gzipSize: true,
                    brotliSize: true,
                    filename: "stats.html",
                }),
            ],
            resolve: {
                alias: {
                    "@": path.resolve(__dirname, "./src"),
                },
            },
            build: {
                outDir: "docker/dist",
                rollupOptions: {
                    output: {
                        manualChunks: (id) => {
                            if (id.includes("node_modules")) {
                                if (id.includes("ionicons5")) {
                                    if (/[A-C].*$/.test(id.split("/").pop())) {
                                        return "ionicons5_A_C";
                                    }
                                    if (/[D-K].*$/.test(id.split("/").pop())) {
                                        return "ionicons5_D_K";
                                    }
                                    if (/[L-M].*$/.test(id.split("/").pop())) {
                                        return "ionicons5_L_M";
                                    }
                                    return "ionicons5";
                                }
                                if (id.includes("three")) {
                                    if (id.includes("examples")) {
                                        return "three_examples";
                                    }
                                    return "three";
                                }
                                if (id.includes("naive-ui")) {
                                    return "naive_ui";
                                }
                                return "vendor";
                            }
                            return "index";
                        },
                    },
                },
            },
            esbuild: {
                drop: ["console", "debugger"],
            },
        };
    }
});
