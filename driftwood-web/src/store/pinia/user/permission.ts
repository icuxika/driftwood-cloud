import { defineStore } from "pinia";
import { NTag, TreeOption } from "naive-ui";
import { PermissionGroupWithId } from "@/api/modules/user/permission-group";
import { h } from "vue";
import { PermissionWithId } from "@/api/modules/user/permission";

const renderPrefix = (isGroup: boolean) => {
	return h(
		NTag,
		{ type: isGroup ? "info" : "warning" },
		{ default: () => (isGroup ? "权限分组" : "权限") }
	);
};

const renderSuffix = (isGroup: boolean, authority: string) => {
	if (isGroup) {
		return null;
	}
	return h(NTag, { type: "success" }, { default: () => authority });
};

interface DPermissionState {
	permissionData: TreeOption[];
}

export const usePermissionStore = defineStore("permission", {
	state: (): DPermissionState => ({
		permissionData: [],
	}),
	getters: {},
	actions: {
		refreshPermission: async function (
			permissionGroupList: PermissionGroupWithId[],
			permissionList: PermissionWithId[]
		) {
			const treeOptionList: TreeOption[] = [];
			const map = permissionGroupList.reduce<{
				[key: number]: TreeOption;
			}>((previousValue, currentValue) => {
				previousValue[currentValue.id] = {
					label: currentValue.name,
					key: "G" + currentValue.id,
					isGroup: true,
					prefix: () => renderPrefix(true),
					suffix: () => renderSuffix(true, ""),
					children: [],
				};
				return previousValue;
			}, {});

			permissionGroupList.forEach((permissionGroup) => {
				if (permissionGroup.parentId === 0) {
					treeOptionList.push(map[permissionGroup.id]);
				} else {
					const parentPermissionGroup = map[permissionGroup.parentId];
					parentPermissionGroup.children?.push(
						map[permissionGroup.id]
					);
				}
			});

			permissionList.forEach((permission) => {
				const treeOption: TreeOption = {
					label: permission.name,
					key: permission.id,
					isGroup: false,
					prefix: () => renderPrefix(false),
					suffix: () => renderSuffix(false, permission.authority),
				};
				if (permission.groupId === 0) {
					treeOptionList.push(treeOption);
				} else {
					const permissionGroup = map[permission.groupId];
					permissionGroup.children?.push(treeOption);
				}
			});
			this.permissionData = treeOptionList;
		},
	},
});
