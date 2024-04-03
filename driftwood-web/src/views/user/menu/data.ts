interface MenuFormModel {
    id: number | null;
    parentId: number;
    type: number;
    name: string;
    icon: string;
    path: string;
}

const defaultMenuFormModel: MenuFormModel = {
    id: null,
    parentId: 0,
    type: 1,
    name: "",
    icon: "",
    path: "",
};

const menuFormModel: MenuFormModel = {
    ...defaultMenuFormModel,
};

export { defaultMenuFormModel, menuFormModel };
export type { MenuFormModel };
