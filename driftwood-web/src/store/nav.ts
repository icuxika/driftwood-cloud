import { defineStore } from "pinia";
import {
    _RouteLocationBase,
    RouteMeta,
    RouteRecordNormalized,
} from "vue-router";

export type NavRoute = (RouteRecordNormalized | _RouteLocationBase) & {
    path: string;
    meta: RouteMeta;
};

interface NavState {
    navRouteList: NavRoute[];
    keepAliveInclude: string;
}

export const useNavStore = defineStore("nav", {
    state: (): NavState => ({
        navRouteList: [],
        keepAliveInclude: "",
    }),
    getters: {},
    actions: {
        /**
         * 初始化动态导航栏
         */
        initNavRoute(navRouteList: NavRoute[]) {
            this.navRouteList = navRouteList;
        },

        /**
         * 添加导航标签
         */
        addNavRoute(navRoute: NavRoute) {
            const exist = this.navRouteList.find(
                (item) => item.path === navRoute.path
            );
            if (!exist) {
                this.navRouteList.push(navRoute);
                if (navRoute.name) {
                    this.addKeepAliveInclude(navRoute.name.toString());
                }
            }
        },

        /**
         * 删除导航标签
         */
        removeNavRoute(path: string) {
            const index = this.navRouteList.findIndex(
                (item) => item.path === path
            );
            if (index !== -1) {
                const routeName = this.navRouteList[index].name;
                if (routeName) {
                    this.removeKeepAliveInclude(routeName.toString());
                }
                this.navRouteList.splice(index, 1);
            }
        },

        addKeepAliveInclude(name: string) {
            if (this.keepAliveInclude === "") {
                this.keepAliveInclude = name;
            } else {
                const cache = this.keepAliveInclude.split(",");
                cache.push(name);
                this.keepAliveInclude = cache.join(",");
            }
        },

        removeKeepAliveInclude(name: string) {
            const cache = this.keepAliveInclude.split(",");
            const removeIndex = cache.indexOf(name);
            if (removeIndex !== -1) {
                cache.splice(removeIndex, 1);
            }
            this.keepAliveInclude = cache.join(",");
        },
    },
});
