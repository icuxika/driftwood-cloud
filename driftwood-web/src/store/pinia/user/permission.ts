import { defineStore } from "pinia";
import { NTag, TreeOption } from "naive-ui";
import {
	PermissionGroup,
	permissionGroupService,
	PermissionGroupWithId,
} from "@/api/modules/user/permission-group";
import { h } from "vue";
import {
	Permission,
	permissionService,
	PermissionWithId,
} from "@/api/modules/user/permission";
import { resolveAxiosResult } from "@/api";

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
	cachePermissionGroupList: PermissionGroupWithId[];
	cachePermissionList: PermissionWithId[];
}

export const usePermissionStore = defineStore("permission", {
	state: (): DPermissionState => ({
		permissionData: [],
		cachePermissionGroupList: [],
		cachePermissionList: [],
	}),
	getters: {},
	actions: {
		async refreshPermission(
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

		async listPermissionGroup(
			permissionGroup: Partial<PermissionGroup> = {}
		) {
			return resolveAxiosResult(() =>
				permissionGroupService.list(permissionGroup)
			);
		},

		async listPermission(permission: Partial<Permission> = {}) {
			return resolveAxiosResult(() => permissionService.list(permission));
		},

		initCachePermissionGroup(permissionGroupList: PermissionGroupWithId[]) {
			this.cachePermissionGroupList = [...permissionGroupList];
		},

		initCachePermission(permissionList: PermissionWithId[]) {
			this.cachePermissionList = [...permissionList];
		},

		getCachePermissionGroupById(id: PermissionGroupWithId["id"]) {
			return this.cachePermissionGroupList.find(
				(permissionGroup) => permissionGroup.id === id
			);
		},

		getCachePermissionById(id: PermissionWithId["id"]) {
			return this.cachePermissionList.find(
				(permission) => permission.id === id
			);
		},
	},
});
