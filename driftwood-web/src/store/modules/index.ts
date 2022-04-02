import {ActionTree, GetterTree, Module, MutationTree} from "vuex";
import {StateInterface} from "../index";
import {BuiltInGlobalTheme} from "naive-ui/es/themes/interface";
import {lightTheme} from "naive-ui/es/themes/light";
import {darkTheme, dateEnUS, dateZhCN, enUS, NDateLocale, NLocale, zhCN} from "naive-ui";

export interface IndexStateInterface {
	naiveUITheme: BuiltInGlobalTheme;
	naiveUILocale: NLocale;
	naiveUIDateLocale: NDateLocale;
}

function state(): IndexStateInterface {
	return {
		naiveUITheme: lightTheme,
		naiveUILocale: zhCN,
		naiveUIDateLocale: dateZhCN
	};
}

const getters: GetterTree<IndexStateInterface, StateInterface> = {};

const mutations: MutationTree<IndexStateInterface> = {
	setTheme(state, theme) {
		state.naiveUITheme = theme;
	},
	setLocale(state, locale) {
		state.naiveUILocale = locale;
	},
	setDateLocale(state, dateLocale) {
		state.naiveUIDateLocale = dateLocale;
	}
};

const actions: ActionTree<IndexStateInterface, StateInterface> = {
	setEnableDarkTheme({commit}, enable: boolean) {
		if (enable) {
			commit("setTheme", darkTheme);
		} else {
			commit("setTheme", lightTheme);
		}
	},
	setEnableEnLocal({commit}, enable: boolean) {
		if (enable) {
			commit("setLocale", enUS);
		} else {
			commit("setLocale", zhCN);
		}
	},
	setEnableEnDateLocal({commit}, enable: boolean) {
		if (enable) {
			commit("setDateLocale", dateEnUS);
		} else {
			commit("setDateLocale", dateZhCN);
		}
	}
};

const indexModule: Module<IndexStateInterface, StateInterface> = {
	namespaced: true,
	state,
	getters,
	mutations,
	actions,
};

export default indexModule;
