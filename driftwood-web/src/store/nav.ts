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
}

export const useNavStore = defineStore("nav", {
	state: (): NavState => ({
		navRouteList: [],
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
				this.navRouteList.splice(index, 1);
			}
		},
	},
});
