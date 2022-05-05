import { ActionTree, GetterTree, Module, MutationTree } from "vuex";
import { StateInterface } from "../index";
import { _RouteLocationBase } from "vue-router";

export interface NavStateInterface {
	navRouteList: _RouteLocationBase[];
}

function state(): NavStateInterface {
	return {
		navRouteList: [],
	};
}

const getters: GetterTree<NavStateInterface, StateInterface> = {};

const mutations: MutationTree<NavStateInterface> = {
	initNavRoute(state, navRouteList) {
		state.navRouteList = navRouteList;
	},
	addNavRoute(state, navRoute) {
		const exist = state.navRouteList.find(
			(item) => item.path === navRoute.path
		);
		if (!exist) {
			state.navRouteList.push(navRoute);
		}
	},
	removeNavRoute(state, path) {
		const index = state.navRouteList.findIndex(
			(item) => item.path === path
		);
		if (index !== -1) {
			state.navRouteList.splice(index, 1);
		}
	},
};

const actions: ActionTree<NavStateInterface, StateInterface> = {
	/**
	 * 初始化动态导航栏
	 */
	initNavRoute({ commit }, navRouteList: _RouteLocationBase[]) {
		commit("initNavRoute", navRouteList);
	},

	/**
	 * 添加导航标签
	 */
	addNavRoute({ commit }, navRoute: _RouteLocationBase) {
		commit("addNavRoute", navRoute);
	},

	/**
	 * 删除导航标签
	 */
	removeNavRoute({ commit }, path: string) {
		commit("removeNavRoute", path);
	},
};

const navModule: Module<NavStateInterface, StateInterface> = {
	namespaced: true,
	state,
	getters,
	mutations,
	actions,
};

export default navModule;
