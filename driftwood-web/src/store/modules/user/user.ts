import { ActionTree, GetterTree, Module, MutationTree } from "vuex";
import { StateInterface } from "../../index";
import { User, userService } from "../../../api/modules/user/user";
import { Pageable } from "../../../api";

export interface UserStateInterface {}

function state(): UserStateInterface {
	return {};
}

const getters: GetterTree<UserStateInterface, StateInterface> = {};

const mutations: MutationTree<UserStateInterface> = {};

const actions: ActionTree<UserStateInterface, StateInterface> = {
	/**
	 * 获取用户信息
	 */
	async getUserInfo() {
		userService
			.getUserInfo()
			.then((response) => {
				console.log("获取用户信息成功");
				console.log(response.data.data);
			})
			.catch((error) => {
				console.log(error);
				console.log("获取用户信息失败");
			});
	},

	/**
	 * 获取用户分页数据
	 */
	async page({ commit }, pageable: Pageable | User) {
		userService
			.page(pageable)
			.then((response) => {
				console.log("获取用户分页数据成功");
				console.log(response.data.data);
			})
			.catch((error) => {
				console.log(error);
				console.log("获取用户分页数据失败");
			});
	},
};

const userModule: Module<UserStateInterface, StateInterface> = {
	namespaced: true,
	state,
	getters,
	mutations,
	actions,
};

export default userModule;
