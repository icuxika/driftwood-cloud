interface PermissionGroupFormModel {
    name: string;
    description: string;
}

interface PermissionFormModel {
    name: string;
    authority: string;
    type: number;
    groupId: number;
    description: string;
}

const defaultPermissionGroupFormModel: PermissionGroupFormModel = {
    name: "新增",
    description: "描述",
};

const defaultPermissionFormModel: PermissionFormModel = {
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
    defaultPermissionGroupFormModel,
    defaultPermissionFormModel,
    permissionGroupFormModel,
    permissionFormModel,
};
export type { PermissionGroupFormModel, PermissionFormModel };
