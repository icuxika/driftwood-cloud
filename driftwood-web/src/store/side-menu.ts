import { defineStore } from "pinia";
import { MenuOption, NIcon } from "naive-ui";
import { Component, h } from "vue";
import { RouterLink } from "vue-router";
import { MenuWithId } from "@/api/modules/user/menu";

const renderIcon = (icon: Component) => {
	return () => h(NIcon, null, { default: () => h(icon) });
};

interface SideMenuState {
	/**
	 * 侧边栏菜单树
	 */
	menuOptions: MenuOption[];
}

export const useSideMenuStore = defineStore("side-menu", {
	state: (): SideMenuState => ({
		menuOptions: [],
	}),
	getters: {},
	actions: {
		/**
		 * 刷新菜单
		 */
		async refreshMenu(menuList: MenuWithId[]) {
			const menuOptionList: MenuOption[] = [];

			// id -> icon name
			const iconNameMap = menuList.reduce<{
				[key: number]: string;
			}>((previousValue, currentValue) => {
				if (currentValue.type !== 2) {
					previousValue[currentValue.id] = currentValue.icon;
				}
				return previousValue;
			}, {});

			// id -> icon
			const iconMap = await this.iconMap(iconNameMap);

			// id -> MenuOption
			const menuMap = menuList.reduce<{
				[key: number]: MenuOption;
			}>((previousValue, currentValue) => {
				previousValue[currentValue.id] = {
					...(currentValue.type === 2 && {
						type: "group",
					}),
					...(currentValue.type !== 2 && {
						icon: renderIcon(iconMap[currentValue.id]),
					}),
					key: currentValue.id,
					...(currentValue.type !== 3 && {
						label: currentValue.name,
						children: [],
					}),
					...(currentValue.type === 3 && {
						label: () =>
							h(
								RouterLink,
								{
									to: {
										path: currentValue.path,
									},
								},
								{
									default: () => currentValue.name,
								}
							),
					}),
				};
				return previousValue;
			}, {});

			// build tree
			menuList
				.sort((a, b) => {
					if (a.sequence < b.sequence) {
						return -1;
					}
					if (a.sequence > b.sequence) {
						return 1;
					}
					return 0;
				})
				.forEach((menu) => {
					if (menu.parentId === 0) {
						menuOptionList.push(menuMap[menu.id]);
					} else {
						const parentMenuOption = menuMap[menu.parentId];
						parentMenuOption.children?.push(menuMap[menu.id]);
					}
				});

			// eslint-disable-next-line @typescript-eslint/ban-ts-comment
			// @ts-ignore
			this.menuOptions = menuOptionList;
		},

		async iconMap(iconNameMap: { [key: number]: string }) {
			// icon name -> icon
			const iconMapByName = await this.iconMapByName(
				"@vicons/ionicons5",
				[...new Set(Object.values(iconNameMap))]
			);
			// id -> icon
			const result = <{ [key: number]: Component }>{};
			Object.keys(iconNameMap).forEach((id) => {
				if (iconMapByName[iconNameMap[Number(id)]]) {
					result[Number(id)] = iconMapByName[iconNameMap[Number(id)]];
				}
			});
			return result;
		},

		/**
		 * 根据图标名称加载图标
		 * @param iconPackage 图标 package 标识符，但目前通过参数传递会报错
		 * @param iconNameList 需要使用的图标名称集合，可在 https://www.xicons.org/#/ 上 Copy Name，需注意图标分类要对应使用的 package
		 */
		async iconMapByName(iconPackage: string, iconNameList: string[]) {
			const icons = await import("@vicons/ionicons5");
			// icon name -> icon
			return Object.values(icons)
				.filter((icon) => iconNameList.includes((icon as any).name))
				.reduce<{
					[key: string]: Component;
				}>((previousValue, currentValue) => {
					previousValue[(currentValue as any).name] =
						currentValue as Component;
					return previousValue;
				}, {});
		},
	},
});
