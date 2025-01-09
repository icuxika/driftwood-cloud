import pluginJs from "@eslint/js";
import eslintPluginJsonc from "eslint-plugin-jsonc";
import eslintPluginPrettierRecommended from "eslint-plugin-prettier/recommended";
import pluginVue from "eslint-plugin-vue";
import tseslint from "typescript-eslint";
import vueEslintParser from "vue-eslint-parser";

/** @type {import('eslint').Linter.Config[]} */
export default [
    {
        ignores: ["node_modules/", "docker/", "public/"],
    },
    {
        files: ["**/*.{js,mjs,cjs,ts,jsx,tsx}"],
        languageOptions: {
            parserOptions: { parser: tseslint.parser },
        },
    },
    {
        files: ["**/*.vue"],
        languageOptions: {
            globals: {
                NodeJS: "readonly",
            },
            parser: vueEslintParser,
            parserOptions: { parser: tseslint.parser },
        },
    },
    pluginJs.configs.recommended,
    ...tseslint.configs.recommended,
    ...pluginVue.configs["flat/essential"],
    eslintPluginPrettierRecommended,
    ...eslintPluginJsonc.configs["flat/recommended-with-jsonc"],
    {
        rules: {
            "prettier/prettier": [
                "error",
                {
                    singleQuote: false,
                },
            ],
            "@typescript-eslint/no-unused-vars": "off",
            "@typescript-eslint/no-explicit-any": "off",
            "@typescript-eslint/no-empty-object-type": "off",
            "@typescript-eslint/no-unused-expressions": "off",
            "vue/multi-word-component-names": "off",
        },
    },
];
