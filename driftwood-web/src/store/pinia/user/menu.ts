import { defineStore } from "pinia";
import { NTag, TreeOption } from "naive-ui";
import { Menu, menuService, MenuWithId } from "@/api/modules/user/menu";
import { HasId, resolveAxiosResult } from "@/api";
import { h } from "vue";
import { MenuFormModel } from "@/views/user/menu/data";
import { useTrick } from "@/hooks/use-trick";

const renderPrefix = (type: number) => {
	return h(
		NTag,
		{
			type:
				type == 1
					? "info"
					: type == 2
					? "warning"
					: type == 3
					? "success"
					: "default",
		},
		{
			default: () =>
				type == 1
					? "应用"
					: type == 2
					? "服务"
					: type == 3
					? "URL"
					: "按钮",
		}
	);
};

const { sleep } = useTrick();

interface MenuState {
	menuData: TreeOption[];
	cacheMenuList: MenuWithId[];
}

export const useMenuStore = defineStore("menu", {
	state: (): MenuState => ({
		menuData: [],
		cacheMenuList: [],
	}),
	getters: {
		menuDict(): {
			label: string;
			value: number;
		}[] {
			const dict = this.cacheMenuList.reduce<
				{
					label: string;
					value: number;
				}[]
			>((previousValue, currentValue) => {
				previousValue.push({
					label: currentValue.name,
					value: currentValue.id,
				});
				return previousValue;
			}, []);
			dict.push({
				label: "无",
				value: 0,
			});
			return dict;
		},
	},
	actions: {
		menuTreeOption(menu: MenuWithId): TreeOption {
			return {
				label: menu.name,
				key: menu.id,
				...(menu.type !== 4 && {
					children: [],
				}),
				prefix: () => renderPrefix(menu.type),
				type: menu.type,
				parentId: menu.parentId,
			};
		},

		addTreeOption(parent: TreeOption | TreeOption[], menu: MenuWithId) {
			const option = this.menuTreeOption(menu);
			if (Array.isArray(parent)) {
				parent.push(option);
			} else {
				parent.children?.push(option);
			}
		},

		updateTreeOption(parent: TreeOption | TreeOption[], menu: MenuWithId) {
			const option = this.menuTreeOption(menu);
			if (Array.isArray(parent)) {
				const index = parent.findIndex((p) => p.key === menu.id);
				parent.splice(index, 1);
				parent.splice(index, 0, option);
			} else {
				if (parent.children) {
					const index = parent.children.findIndex(
						(p) => p.key === menu.id
					);
					parent.children.splice(index, 1);
					parent.children.splice(index, 0, option);
				}
			}
		},

		/**
		 * 刷新菜单
		 */
		async refreshMenu(menuList: MenuWithId[]) {
			const treeOptionList: TreeOption[] = [];
			const map = menuList.reduce<{
				[key: number]: TreeOption;
			}>((previousValue, currentValue) => {
				previousValue[currentValue.id] =
					this.menuTreeOption(currentValue);
				return previousValue;
			}, {});

			menuList.forEach((menu) => {
				if (menu.parentId === 0) {
					treeOptionList.push(map[menu.id]);
				} else {
					const parentMenu = map[menu.parentId];
					parentMenu.children?.push(map[menu.id]);
				}
			});

			this.menuData = treeOptionList;
		},

		/**
		 * 获取菜单列表
		 */
		async listMenu(menu: Partial<Menu> = {}) {
			return resolveAxiosResult(() => menuService.list(menu));
		},

		/**
		 * 新增菜单
		 */
		async saveMenu(menuFormModel: MenuFormModel) {
			const newMenu = await resolveAxiosResult(() =>
				menuService.save({
					parentId: menuFormModel.parentId,
					type: menuFormModel.type,
					name: menuFormModel.name,
					icon: menuFormModel.icon,
					path: menuFormModel.path,
				})
			);
			if (newMenu) {
				await this.saveCacheMenu(newMenu);
			}
			return newMenu;
		},

		/**
		 * 更新菜单
		 */
		async updateMenu(menuFormModel: MenuFormModel) {
			const menuFormModelWithId = menuFormModel as MenuFormModel & HasId;
			const newMenu = await resolveAxiosResult(() =>
				menuService.update({
					id: menuFormModelWithId.id,
					parentId: menuFormModelWithId.parentId,
					type: menuFormModelWithId.type,
					name: menuFormModelWithId.name,
					icon: menuFormModelWithId.icon,
					path: menuFormModelWithId.path,
				})
			);
			if (newMenu) {
				this.updateCacheMenu(newMenu);
			}
			return newMenu;
		},

		/**
		 * 删除菜单
		 */
		async deleteMenu(id: MenuWithId["id"]) {
			return sleep()
				.then(() => sleep())
				.then(() => {
					console.log("delete: ", id);
				});
		},

		/**
		 * 初始化菜单缓存
		 */
		initCacheMenu(menuList: MenuWithId[]) {
			this.cacheMenuList = [...menuList];
		},

		/**
		 * 获取菜单缓存
		 */
		getCacheMenuById(id: MenuWithId["id"]) {
			return this.cacheMenuList.find((menu) => menu.id === id);
		},

		/**
		 * 新增菜单缓存
		 */
		saveCacheMenu(menu: MenuWithId) {
			this.cacheMenuList.push(menu);
		},

		/**
		 * 更新菜单缓存
		 */
		updateCacheMenu(menu: MenuWithId) {
			const existMenu = this.getCacheMenuById(menu.id);
			if (existMenu) {
				Object.assign(existMenu, menu);
			}
		},

		/**
		 * 移除菜单缓存
		 */
		removeCacheMenu(id: MenuWithId["id"]) {
			const index = this.cacheMenuList.findIndex(
				(menu) => menu.id === id
			);
			if (index != -1) {
				this.cacheMenuList.splice(index, 1);
			}
		},
	},
});
