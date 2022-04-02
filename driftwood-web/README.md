Vue 3 + Typescript + Vite
==========

## 问题记录

### ESLint

```shell
yarn eslint -c .eslintrc.json vite.config.ts src/**/*.ts src/**/*.tsx src/**/*.vue --fix
```

上面这个命令必须要在terminal中运行才能完整运行，目前有些奇怪的问题，在package.json中配置以下内容也不生效

```shell
"lint": "eslint -c .eslintrc.json vite.config.ts src/**/*.ts src/**/*.tsx src/**/*.vue --fix"
```
