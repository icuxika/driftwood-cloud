import { defineStore } from "pinia";
import { User, userService, UserInfoVO, UserVO } from "@/api/modules/user/user";
import { Page, Pageable, resolveAxiosResult } from "@/api";

interface UserState {
	currentUser?: UserInfoVO;
}

export const useUserStore = defineStore("user", {
	state: (): UserState => ({}),
	getters: {},
	actions: {
		setCurrentUser(currentUser: UserInfoVO) {
			this.currentUser = currentUser;
		},

		/**
		 * 获取用户信息
		 */
		async getUserInfo(): Promise<UserInfoVO | null> {
			return resolveAxiosResult(userService.getUserInfo);
		},

		/**
		 * 获取用户分页数据
		 */
		async page<T extends User>(
			pageable: Partial<Pageable & T>
		): Promise<Page<UserVO> | null> {
			return resolveAxiosResult(() => userService.page(pageable));
		},

		/**
		 * 导出用户数据到 excel
		 */
		async exportExcel<T extends User>(user: Partial<T>) {
			return userService.exportExcel(user);
		},
	},
});
