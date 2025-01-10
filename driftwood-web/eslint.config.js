import pluginJs from "@eslint/js";
import eslintPluginJsonc from "eslint-plugin-jsonc";
import eslintPluginPrettierRecommended from "eslint-plugin-prettier/recommended";
import pluginVue from "eslint-plugin-vue";
import tseslint from "typescript-eslint";
import vueEslintParser from "vue-eslint-parser";

/**
 * 定义ESLint配置数组，用于指定代码检查规则和忽略项
 * @type {import('eslint').Linter.Config[]}
 */
export default [
    {
        // 忽略特定目录下的文件，这些目录通常包含第三方代码或配置
        ignores: ["node_modules/", "docker/", "public/"],
    },
    {
        // 指定需要检查的JavaScript和TypeScript文件路径模式
        files: ["**/*.{js,mjs,cjs,ts,jsx,tsx}"],
        languageOptions: {
            // 配置解析器选项，使用tseslint解析器
            parserOptions: { parser: tseslint.parser },
        },
    },
    {
        // 指定需要检查的Vue文件路径模式
        files: ["**/*.vue"],
        languageOptions: {
            // 在Vue文件中配置全局变量和解析器
            globals: {
                NodeJS: "readonly",
            },
            parser: vueEslintParser,
            // 使用tseslint解析器解析Vue文件中的TypeScript代码
            parserOptions: { parser: tseslint.parser },
        },
    },
    // 引入推荐的JavaScript ESLint配置
    pluginJs.configs.recommended,
    // 引入推荐的TypeScript ESLint配置
    ...tseslint.configs.recommended,
    // 引入Vue推荐的ESLint配置
    ...pluginVue.configs["flat/essential"],
    // 引入推荐的Prettier ESLint配置，用于格式化代码
    eslintPluginPrettierRecommended,
    // 引入推荐的JSONC ESLint配置，用于处理JSON文件中的注释
    ...eslintPluginJsonc.configs["flat/recommended-with-jsonc"],
    {
        rules: {
            // 配置Prettier规则，使用双引号，不允许未使用的变量
            "prettier/prettier": [
                "error",
                {
                    singleQuote: false,
                },
            ],
            // 关闭TypeScript ESLint插件的某些规则，以减少开发过程中的限制
            "@typescript-eslint/no-unused-vars": "off",
            "@typescript-eslint/no-explicit-any": "off",
            "@typescript-eslint/no-empty-object-type": "off",
            "@typescript-eslint/no-unused-expressions": "off",
            "vue/multi-word-component-names": "off",
        },
    },
];
