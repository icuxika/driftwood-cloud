interface PermissionGroupFormModel {
    id: number;
    name: string;
    parentId: number;
    description: string;
}

interface PermissionFormModel {
    id: number;
    name: string;
    authority: string;
    type: number;
    groupId: number;
    description: string;
}

const defaultPermissionGroupFormModel: PermissionGroupFormModel = {
    id: 0,
    name: "新增",
    parentId: 1,
    description: "模块",
};

const defaultPermissionFormModel: PermissionFormModel = {
    id: 0,
    name: "新增",
    authority: "user:user:add",
    type: 1,
    groupId: 0,
    description: "描述",
};

const permissionGroupFormModel: PermissionGroupFormModel = {
    ...defaultPermissionGroupFormModel,
};

const permissionFormModel: PermissionFormModel = {
    ...defaultPermissionFormModel,
};

export {
    defaultPermissionFormModel,
    defaultPermissionGroupFormModel,
    permissionFormModel,
    permissionGroupFormModel,
};
export type { PermissionFormModel, PermissionGroupFormModel };
