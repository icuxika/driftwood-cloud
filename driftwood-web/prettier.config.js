/**
 * Prettier配置对象，用于定义代码格式化的各种规则
 * @see https://prettier.io/docs/en/configuration.html
 * @type {import("prettier").Config}
 */
const config = {
    // 是否使用实验性三元表达式语法
    experimentalTernaries: false,
    // 每行打印的字符宽度
    printWidth: 80,
    // 一个制表符的空格数量
    tabWidth: 4,
    // 是否使用制表符进行缩进
    useTabs: false,
    // 是否在语句结尾添加分号
    semi: true,
    // 是否使用单引号代替双引号
    singleQuote: false,
    // 何时需要给对象属性加引号
    quoteProps: "as-needed",
    // 在JSX中是否使用单引号
    jsxSingleQuote: false,
    // 是否在对象、数组的最后一个元素后添加逗号
    trailingComma: "es5",
    // 在对象、数组的括号之间插入空格
    bracketSpacing: true,
    // 是否将对象、数组的结束括号与开始括号对齐
    bracketSameLine: false,
    // 箭头函数的参数是否需要括号
    arrowParens: "always",
    // 格式化代码的起始行数
    rangeStart: 0,
    // 格式化代码的结束行数
    rangeEnd: Infinity,
    // 使用的解析器
    // parser: null,
    // 文件路径，用于解析某些语言特定的格式化规则
    // filepath: null,
    // 是否需要在文件中包含特定的注释（如eslint的pragma注释）
    requirePragma: false,
    // 是否在文件中插入特定的注释
    insertPragma: false,
    // 如何处理散文中的换行
    proseWrap: "preserve",
    // HTML中空白字符的敏感度
    htmlWhitespaceSensitivity: "css",
    // 是否给Vue的<script>和<style>标签单独缩进
    vueIndentScriptAndStyle: false,
    // 文件末尾的换行方式
    endOfLine: "lf",
    // 格式化嵌入式语言的方式
    embeddedLanguageFormatting: "auto",
    // 是否每个属性占一行
    singleAttributePerLine: false,
};

export default config;
