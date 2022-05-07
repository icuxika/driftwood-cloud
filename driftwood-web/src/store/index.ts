import { InjectionKey } from "vue";
import { createStore, Store, useStore as vuexUseStore } from "vuex";
import indexModule, { IndexStateInterface } from "@/store/modules";
import authModule, { AuthStateInterface } from "@/store/modules/auth";
import userModule, { UserStateInterface } from "@/store/modules/user/user";
import navModule, { NavStateInterface } from "@/store/modules/nav";

declare module "@vue/runtime-core" {
	interface ComponentCustomProperties {
		$store: Store<StateInterface>;
	}
}

export interface StateInterface {
	index: IndexStateInterface;
	auth: AuthStateInterface;
	user: UserStateInterface;
	nav: NavStateInterface;
}

export const key: InjectionKey<Store<StateInterface>> = Symbol("vuex-key");

export const store = createStore<StateInterface>({
	modules: {
		index: indexModule,
		auth: authModule,
		user: userModule,
		nav: navModule,
	},
});

export function useStore() {
	return vuexUseStore(key);
}
