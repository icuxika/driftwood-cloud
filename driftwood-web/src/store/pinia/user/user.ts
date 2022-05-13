import { defineStore } from "pinia";
import { User, userService, UserVO } from "@/api/modules/user/user";
import { ApiData, Page, Pageable } from "@/api";

interface UserState {
	currentUser?: UserVO;
}

export const useUserStore = defineStore("user", {
	state: (): UserState => ({}),
	getters: {},
	actions: {
		setCurrentUser(currentUser: UserVO) {
			this.currentUser = currentUser;
		},

		/**
		 * 获取用户信息
		 */
		async getUserInfo(): Promise<ApiData<UserVO>> {
			try {
				const response = await userService.getUserInfo();
				return response.data;
			} catch (error) {
				return Promise.reject(error);
			}
		},

		/**
		 * 获取用户分页数据
		 */
		async page<T extends User>(
			pageable: Partial<Pageable & T>
		): Promise<ApiData<Page<User>>> {
			try {
				const response = await userService.page(pageable);
				return response.data;
			} catch (error) {
				return Promise.reject(error);
			}
		},
	},
});
