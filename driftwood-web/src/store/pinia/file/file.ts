import { defineStore } from "pinia";

interface FileState {}

export const useFileStore = defineStore("file", {
	state: (): FileState => ({}),
	getters: {},
	actions: {},
});
