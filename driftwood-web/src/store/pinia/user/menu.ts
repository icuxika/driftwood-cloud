import { defineStore } from "pinia";

interface MenuState {}

export const useMenuStore = defineStore("menu", {
	state: (): MenuState => ({}),
	getters: {},
	actions: {},
});
