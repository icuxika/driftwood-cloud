import { Ref } from "vue";

interface BasicInformation {
    companyName: string;
    creditCode: string;
    calculateYear: string;
    contactInformation: string;
    industry: string;
}

interface ParameterCalculationVO {
    id: string;
    name: string;
    industryId: string;
    version: string;
    description: string;
    categoryList: ParameterCategoryCalculationVO[];
}

interface ParameterCategoryCalculationVO {
    id: string;
    name: string;
    industryId: string;
    parameterId: string;
    parentId: string;
    itemList: ParameterItemCalculationVO[];
}

interface ParameterItemCalculationVO {
    id: string;
    name: string;
}

interface ParameterCategoryVOTreeItem {
    item: ParameterCategoryCalculationVO;
    children: ParameterCategoryVOTreeItem[];
    count: number;
}

interface DataItem {
    serialNo: number;
    categories: string[]; // 从根目录到输入源的父级目录的所有目录名
    categoryFlags: boolean[]; // categories中的每一个格子是否是表格某一端跨行的开始
    categoryItemSize: number[]; // categories中的每一个格子对应的目录具有的输入源（不是目录）的个数
    item: ParameterItemCalculationVO;
    usageAmount: Ref<number>;
}

interface ParameterCalculationDTO {
    id: string;
    companyName: string;
    creditCode: string;
    calculateYear: string;
    contactPerson: string;
    contactInformation: string;
    parameterItemList: ParameterItemCalculationDTO[];
}

interface ParameterItemCalculationDTO {
    id: string;
    usageAmount: number;
}

interface FormulaUnit {
    id: string;
    type: string;
    formula: string;
    env: { [key: string]: number };
    result: number;
}

interface CalculationResultVO {
    formulaUnitList: FormulaUnit[];
}

const data: ParameterCalculationVO = {
    id: "1589889949943513090",
    name: "2022电力测算基本参数",
    industryId: "1589889949876404225",
    version: "GB2022010001",
    description: "2022电力测算基本参数",
    categoryList: [
        {
            id: "1589889950006427650",
            name: "电力",
            industryId: "1589889949876404225",
            parameterId: "1589889949943513090",
            parentId: "0",
            itemList: [
                {
                    id: "1589889950044176385",
                    name: "购入电力",
                },
            ],
        },
        {
            id: "1589889950132256770",
            name: "化石燃料",
            industryId: "1589889949876404225",
            parameterId: "1589889949943513090",
            parentId: "0",
            itemList: [],
        },
        {
            id: "1589889950170005506",
            name: "煤",
            industryId: "1589889949876404225",
            parameterId: "1589889949943513090",
            parentId: "1589889950132256770",
            itemList: [
                {
                    id: "1589889950274863105",
                    name: "原煤",
                },
                {
                    id: "1589889950501355522",
                    name: "洗精煤",
                },
                {
                    id: "1589889950715265026",
                    name: "其它洗煤",
                },
                {
                    id: "1589889950924980226",
                    name: "型煤",
                },
                {
                    id: "1589889951126306817",
                    name: "煤矸石",
                },
                {
                    id: "1589889951331827714",
                    name: "焦炭",
                },
                {
                    id: "1589889951554125826",
                    name: "其它焦化产品",
                },
            ],
        },
        {
            id: "1589889950211948546",
            name: "气",
            industryId: "1589889949876404225",
            parameterId: "1589889949943513090",
            parentId: "1589889950132256770",
            itemList: [
                {
                    id: "1589889951789006849",
                    name: "液化石油气",
                },
                {
                    id: "1589889951944196097",
                    name: "炼厂干气",
                },
                {
                    id: "1589889952111968258",
                    name: "天然气",
                },
                {
                    id: "1589889952283934722",
                    name: "液化天然气",
                },
                {
                    id: "1589889952439123969",
                    name: "焦炉煤气",
                },
                {
                    id: "1589889952590118913",
                    name: "高炉煤气",
                },
                {
                    id: "1589889952741113858",
                    name: "转炉煤气",
                },
                {
                    id: "1589889952896303106",
                    name: "其他煤气",
                },
            ],
        },
        {
            id: "1589889950245502978",
            name: "油",
            industryId: "1589889949876404225",
            parameterId: "1589889949943513090",
            parentId: "1589889950132256770",
            itemList: [
                {
                    id: "1589889953055686658",
                    name: "原油",
                },
                {
                    id: "1589889953210875906",
                    name: "汽油",
                },
                {
                    id: "1589889953361870849",
                    name: "煤油",
                },
                {
                    id: "1589889953496088578",
                    name: "柴油",
                },
                {
                    id: "1589889953626112002",
                    name: "燃料油",
                },
                {
                    id: "1589889953751941122",
                    name: "石油焦",
                },
                {
                    id: "1589889953881964546",
                    name: "其它石油制品",
                },
            ],
        },
    ],
};

export { data };

export type {
    BasicInformation,
    CalculationResultVO,
    DataItem,
    FormulaUnit,
    ParameterCalculationDTO,
    ParameterCalculationVO,
    ParameterCategoryCalculationVO,
    ParameterCategoryVOTreeItem,
    ParameterItemCalculationDTO,
    ParameterItemCalculationVO,
};
