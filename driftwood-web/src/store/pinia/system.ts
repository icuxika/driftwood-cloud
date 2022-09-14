import { defineStore } from "pinia";
import { BuiltInGlobalTheme } from "naive-ui/es/themes/interface";
import { lightTheme } from "naive-ui";
import {
	darkTheme,
	dateEnUS,
	dateZhCN,
	enUS,
	NDateLocale,
	NLocale,
	zhCN,
} from "naive-ui";

interface SystemState {
	naiveUITheme: BuiltInGlobalTheme;
	naiveUILocale: NLocale;
	naiveUIDateLocale: NDateLocale;
}

export const useSystemStore = defineStore("system", {
	state: (): SystemState => ({
		naiveUITheme: lightTheme,
		naiveUILocale: zhCN,
		naiveUIDateLocale: dateZhCN,
	}),
	getters: {},
	actions: {
		/**
		 * 是否启用 Naïve UI 暗色主题
		 */
		setDarkThemeEnable(enable: boolean) {
			if (enable) {
				this.naiveUITheme = darkTheme;
			} else {
				this.naiveUITheme = lightTheme;
			}
		},

		/**
		 * 是否启用 Naïve UI 全局英文
		 */
		setEnLocaleEnable(enable: boolean) {
			if (enable) {
				this.naiveUILocale = enUS;
			} else {
				this.naiveUILocale = zhCN;
			}
		},

		/**
		 * 是否启用 Naïve UI 全局日期英文
		 */
		setEnDateLocalEnable(enable: boolean) {
			if (enable) {
				this.naiveUIDateLocale = dateEnUS;
			} else {
				this.naiveUIDateLocale = dateZhCN;
			}
		},
	},
});
