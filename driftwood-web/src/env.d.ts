/// <reference types="vite/client" />

declare module "*.vue" {
    import type { DefineComponent } from "vue";
    const component: DefineComponent<{}, {}, any>;
    export default component;
}

interface ImportMetaEnv {
    readonly VITE_APP_BASE_URL_PLACEHOLDER: string;
    readonly VITE_APP_BASE_URL: string;
    readonly VITE_APP_ROUTER_BASE: string;
    readonly VITE_GITHUB_CLIENT_ID: string;
    readonly VITE_GITHUB_REDIRECT_URI: string;
    readonly VITE_GITEE_CLIENT_ID: string;
    readonly VITE_GITEE_REDIRECT_URI: string;
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}
