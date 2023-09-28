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
import { HasId, resolveAxiosResult } from "@/api";
import {
	PermissionFormModel,
	PermissionGroupFormModel,
} from "@/views/user/permission/data";

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

		async savePermissionGroup(
			permissionGroupFormModel: PermissionGroupFormModel
		) {
			const newPermissionGroup = await resolveAxiosResult(() =>
				permissionGroupService.save({
					name: permissionGroupFormModel.name,
					description: permissionGroupFormModel.description,
				})
			);
			if (newPermissionGroup) {
				this.saveCachePermissionGroup(newPermissionGroup);
			}
			return newPermissionGroup;
		},

		async savePermission(permissionFormModel: PermissionFormModel) {
			const newPermission = await resolveAxiosResult(() =>
				permissionService.save({
					name: permissionFormModel.name,
					authority: permissionFormModel.authority,
					type: permissionFormModel.type,
					groupId: permissionFormModel.groupId,
					description: permissionFormModel.description,
				})
			);
			if (newPermission) {
				this.saveCachePermission(newPermission);
			}
			return newPermission;
		},

		async updatePermissionGroup(
			permissionGroupFormModel: PermissionGroupFormModel
		) {
			const permissionGroupFormModelWithId =
				permissionGroupFormModel as PermissionGroupFormModel & HasId;
			const newPermissionGroup = await resolveAxiosResult(() =>
				permissionGroupService.update({
					id: permissionGroupFormModelWithId.id,
					name: permissionGroupFormModelWithId.name,
					description: permissionGroupFormModelWithId.description,
				})
			);
			if (newPermissionGroup) {
				this.updateCachePermissionGroup(newPermissionGroup);
			}
			return newPermissionGroup;
		},

		async updatePermission(permissionFormModel: PermissionFormModel) {
			const permissionFormModelWithId =
				permissionFormModel as PermissionFormModel & HasId;
			const newPermission = await resolveAxiosResult(() =>
				permissionService.update({
					id: permissionFormModelWithId.id,
					name: permissionFormModelWithId.name,
					authority: permissionFormModelWithId.authority,
					type: permissionFormModelWithId.type,
					groupId: permissionFormModelWithId.groupId,
					description: permissionFormModelWithId.description,
				})
			);
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

		saveCachePermissionGroup(permissionGroup: PermissionGroupWithId) {
			this.cachePermissionGroupList.push(permissionGroup);
		},

		saveCachePermission(permission: PermissionWithId) {
			this.cachePermissionList.push(permission);
		},

		updateCachePermissionGroup(permissionGroup: PermissionGroupWithId) {
			const existPermissionGroup = this.getCachePermissionGroupById(
				permissionGroup.id
			);
			if (existPermissionGroup) {
				Object.assign(existPermissionGroup, permissionGroup);
			}
		},

		updateCachePermission(permission: PermissionWithId) {
			const existPermission = this.getCachePermissionById(permission.id);
			if (existPermission) {
				Object.assign(existPermission, permission);
			}
		},
	},
});
