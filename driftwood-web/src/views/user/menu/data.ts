interface MenuFormModel {
    id: number;
    parentId: number;
    type: number;
    name: string;
    icon: string;
    path: string;
    sequence: number;
}

const defaultMenuFormModel: MenuFormModel = {
    id: 0,
    parentId: 0,
    type: 1,
    name: "",
    icon: "",
    path: "",
    sequence: 0,
};

const menuFormModel: MenuFormModel = {
    ...defaultMenuFormModel,
};

export { defaultMenuFormModel, menuFormModel };
export type { MenuFormModel };
