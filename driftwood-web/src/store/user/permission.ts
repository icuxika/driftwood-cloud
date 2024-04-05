import { HasId, resolveAxiosResult } from "@/api";
import {
    Permission,
    PermissionWithId,
    permissionService,
} from "@/api/modules/user/permission";
import {
    PermissionGroup,
    PermissionGroupWithId,
    permissionGroupService,
} from "@/api/modules/user/permission-group";
import {
    PermissionFormModel,
    PermissionGroupFormModel,
} from "@/views/user/permission/data";
import { NTag, TreeOption } from "naive-ui";
import { defineStore } from "pinia";
import { h } from "vue";

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
    /**
     * 缓存权限分组列表
     */
    cachePermissionGroupList: PermissionGroupWithId[];
    /**
     * 缓存权限列表
     */
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
        /**
         * 刷新权限
         */
        async refreshPermission() {
            const treeOptionList: TreeOption[] = [];
            const map = this.cachePermissionGroupList.reduce<{
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

            this.cachePermissionGroupList.forEach((permissionGroup) => {
                if (permissionGroup.parentId === 0) {
                    treeOptionList.push(map[permissionGroup.id]);
                } else {
                    const parentPermissionGroup = map[permissionGroup.parentId];
                    parentPermissionGroup.children?.push(
                        map[permissionGroup.id]
                    );
                }
            });

            this.cachePermissionList.forEach((permission) => {
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

        /**
         * 递归查询permissionData及其children中的数据得到权限分组和权限数据
         */
        async saveAllPermissionGroupAndPermission() {
            const treeOptionList: TreeOption[] = [];
            const flattenTree = (tree: TreeOption[], parentId: number) => {
                tree.forEach((treeOption) => {
                    treeOptionList.push(treeOption);
                    if (treeOption.isGroup) {
                        const childParentId = Number(
                            (treeOption.key as string).substring(1)
                        );
                        const cachePermissionGroup =
                            this.getCachePermissionGroupById(childParentId);
                        if (cachePermissionGroup) {
                            cachePermissionGroup.parentId = parentId;
                        }
                        if (treeOption.children) {
                            flattenTree(treeOption.children, childParentId);
                        }
                    } else {
                        const cachePermission = this.getCachePermissionById(
                            treeOption.key as number
                        );
                        if (cachePermission) {
                            cachePermission.groupId = parentId;
                        }
                    }
                });
            };
            flattenTree(this.permissionData, 0);
            console.log(treeOptionList);
            this.refreshPermission();
            await this.updateAllPermission(
                this.cachePermissionGroupList,
                this.cachePermissionList
            );
        },

        /**
         * 获取权限分组列表
         * @param permissionGroup 权限分组
         */
        async listPermissionGroup(
            permissionGroup: Partial<PermissionGroup> = {}
        ) {
            return resolveAxiosResult(() =>
                permissionGroupService.list(permissionGroup)
            );
        },

        /**
         * 获取权限列表
         * @param permission 权限
         */
        async listPermission(permission: Partial<Permission> = {}) {
            return resolveAxiosResult(() => permissionService.list(permission));
        },

        async updateAllPermission(
            permissionGroupList: PermissionGroupWithId[],
            permissionList: PermissionWithId[]
        ) {
            return resolveAxiosResult(() =>
                permissionService.updateAllPermission({
                    permissionGroupList,
                    permissionList,
                })
            );
        },

        /**
         * 保存权限分组
         */
        async savePermissionGroup(
            permissionGroupFormModel: PermissionGroupFormModel
        ) {
            const newPermissionGroup = await resolveAxiosResult(() =>
                permissionGroupService.save({
                    name: permissionGroupFormModel.name,
                    parentId: permissionGroupFormModel.parentId,
                    description: permissionGroupFormModel.description,
                })
            );
            if (newPermissionGroup) {
                this.saveCachePermissionGroup(newPermissionGroup);
            }
            this.refreshPermission();
            return newPermissionGroup;
        },

        /**
         * 保存权限
         */
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
            this.refreshPermission();
            return newPermission;
        },

        /**
         * 更新权限分组
         */
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
            this.refreshPermission();
            return newPermissionGroup;
        },

        /**
         * 更新权限
         */
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
            if (newPermission) {
                this.updateCachePermission(newPermission);
            }
            this.refreshPermission();
            return newPermission;
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
