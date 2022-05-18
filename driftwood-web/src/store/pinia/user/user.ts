import { defineStore } from "pinia";
import { User, userService, UserVO } from "@/api/modules/user/user";
import { Page, Pageable, resolveAxiosResult } from "@/api";

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
		getUserInfo: async (): Promise<UserVO | null> =>
			resolveAxiosResult(userService.getUserInfo),

		/**
		 * 获取用户分页数据
		 */
		page: async <T extends User>(
			pageable: Partial<Pageable & T>
		): Promise<Page<User> | null> =>
			resolveAxiosResult(() => userService.page(pageable)),
	},
});
