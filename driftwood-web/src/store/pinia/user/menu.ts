import { defineStore } from "pinia";
import { NTag, TreeOption } from "naive-ui";
import { Menu, menuService, MenuWithId } from "@/api/modules/user/menu";
import { resolveAxiosResult } from "@/api";
import { h } from "vue";

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

interface MenuState {
	menuData: TreeOption[];
	cacheMenuList: MenuWithId[];
}

export const useMenuStore = defineStore("menu", {
	state: (): MenuState => ({
		menuData: [],
		cacheMenuList: [],
	}),
	getters: {},
	actions: {
		async refreshMenu(menuList: MenuWithId[]) {
			const treeOptionList: TreeOption[] = [];
			const map = menuList.reduce<{
				[key: number]: TreeOption;
			}>((previousValue, currentValue) => {
				previousValue[currentValue.id] = {
					label: currentValue.name,
					key: currentValue.id,
					...(currentValue.type !== 4 && {
						children: [],
					}),
					prefix: () => renderPrefix(currentValue.type),
					type: currentValue.type,
				};
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

		async listMenu(menu: Partial<Menu> = {}) {
			return resolveAxiosResult(() => menuService.list(menu));
		},

		getCacheMenuById(id: MenuWithId["id"]) {
			return this.cacheMenuList.find((menu) => menu.id === id);
		},
	},
});
