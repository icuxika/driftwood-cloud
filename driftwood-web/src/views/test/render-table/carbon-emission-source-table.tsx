import {
    DataItem,
    ParameterCategoryCalculationVO,
    ParameterCategoryVOTreeItem,
} from "@/views/test/render-table/data";
import { NButton, NInputNumber, NTable } from "naive-ui";
import { computed, ComputedRef, defineComponent, PropType, ref } from "vue";

export const CarbonEmissionSourceTable = defineComponent({
    props: {
        previous: {
            type: Function as PropType<(e: MouseEvent) => void>,
            required: true,
        },
        submit: {
            type: Function as PropType<(dataItems: DataItem[]) => void>,
            required: true,
        },
        categoryList: {
            type: Array as PropType<ParameterCategoryCalculationVO[]>,
            required: true,
        },
    },
    setup(props) {
        const headColspan = ref<number>(0);
        const dataItemListRef: ComputedRef<DataItem[]> = computed(() => {
            if (props.categoryList.length === 0) {
                return [];
            }
            // 同级别结构转化为树结构
            const categoryTree: ParameterCategoryVOTreeItem[] = [];
            const map = props.categoryList.reduce<{
                [key: string]: ParameterCategoryVOTreeItem;
            }>((previousValue, currentValue) => {
                previousValue[currentValue.id] = {
                    item: currentValue,
                    children: [],
                    count: 0,
                };
                return previousValue;
            }, {});

            props.categoryList.forEach((category) => {
                if (category.parentId === "0") {
                    categoryTree.push(map[category.id]);
                } else {
                    const parent = map[category.parentId];
                    parent.children.push(map[category.id]);
                }
            });

            // 计算每个树节点具有的所有子节点（输入项）的个数
            const calcCount = (tree: ParameterCategoryVOTreeItem[]): number => {
                return tree.reduce<number>((total, cur) => {
                    if (cur.children.length === 0) {
                        cur.count = cur.item.itemList.length;
                        return total + cur.item.itemList.length;
                    }
                    cur.count = calcCount(cur.children);
                    return total + cur.count;
                }, 0);
            };
            calcCount(categoryTree);

            // 将tree映射成map方便根据id查询
            const countMap: { [key: string]: ParameterCategoryVOTreeItem } = {};
            const recursive2Map = (t: ParameterCategoryVOTreeItem[]) => {
                t.forEach((i) => {
                    countMap[i.item.id] = i;
                    recursive2Map(i.children);
                });
            };
            recursive2Map(categoryTree);

            // 序号
            let serial = 1;

            // 递归生成指定的数据结构来动态的创建输入源表格
            const recursive = (
                tree: ParameterCategoryVOTreeItem[],
                dataItems: DataItem[],
                cacheNameList: string[],
                cacheFlagList: boolean[]
            ) => {
                tree.forEach((treeItem, index1) => {
                    const names = [...cacheNameList, treeItem.item.name];

                    // 更新标题colspan
                    if (names.length > headColspan.value) {
                        headColspan.value = names.length;
                    }

                    // 判断是否为table一列的开始项
                    const tempFlagList = [...cacheFlagList];
                    if (tempFlagList.length > 0) {
                        tempFlagList[tempFlagList.length - 1] = index1 === 0;
                    }
                    const startFlag =
                        treeItem.item.parentId === "0" ? true : index1 === 0;
                    const flags = [...tempFlagList, startFlag];

                    // 设置表格跨格数
                    const sizes: number[] = [];
                    if (treeItem.item.parentId === "0") {
                        sizes.push(treeItem.count);
                    }
                    let tempTreeItem: ParameterCategoryVOTreeItem = treeItem;
                    while (tempTreeItem.item.parentId !== "0") {
                        sizes.splice(0, 0, tempTreeItem.count);
                        if (countMap[tempTreeItem.item.parentId]) {
                            tempTreeItem = countMap[tempTreeItem.item.parentId];
                            sizes.splice(0, 0, tempTreeItem.count);
                        }
                    }

                    if (treeItem.children.length === 0) {
                        const parameterCategoryVO = treeItem.item;
                        parameterCategoryVO.itemList.forEach(
                            (parameterItem, index2) => {
                                const finalFlagList = [...flags];
                                if (index2 === 0) {
                                    finalFlagList[finalFlagList.length - 1] =
                                        true;
                                } else {
                                    finalFlagList[finalFlagList.length - 1] =
                                        false;
                                    if (finalFlagList.length > 1) {
                                        finalFlagList[
                                            finalFlagList.length - 2
                                        ] = false;
                                    }
                                }

                                const dataItem: DataItem = {
                                    serialNo: serial++,
                                    categories: names,
                                    categoryFlags: finalFlagList,
                                    categoryItemSize: sizes,
                                    item: parameterItem,
                                    usageAmount: ref<number>(0),
                                };
                                dataItems.push(dataItem);
                            }
                        );
                    } else {
                        recursive(treeItem.children, dataItems, names, flags);
                    }
                });
            };

            const dataItemList: DataItem[] = [];
            recursive(categoryTree, dataItemList, [], []);
            return dataItemList;
        });

        return () => (
            <div>
                <NTable
                    bordered={true}
                    singleLine={false}
                    singleColumn={false}
                    size={"small"}
                    style={"width: 800px"}
                >
                    <thead>
                        <tr>
                            <th>序号</th>
                            <th colspan={headColspan.value + 1}>指标体系</th>
                            <th>使用量</th>
                        </tr>
                    </thead>
                    <tbody>
                        {dataItemListRef.value.map((value, index) => {
                            return (
                                <tr>
                                    <td>{value.serialNo}</td>
                                    {value.categories.map(
                                        (categoryName, categoryIndex) => {
                                            if (
                                                value.categoryFlags[
                                                    categoryIndex
                                                ]
                                            ) {
                                                return (
                                                    <td
                                                        colspan={
                                                            headColspan.value -
                                                            value.categoryFlags
                                                                .length +
                                                            1
                                                        }
                                                        rowspan={
                                                            value
                                                                .categoryItemSize[
                                                                categoryIndex
                                                            ]
                                                        }
                                                    >
                                                        {categoryName}
                                                    </td>
                                                );
                                            }
                                        }
                                    )}
                                    <td>{value.item.name}</td>
                                    <td>
                                        <NInputNumber
                                            v-model:value={
                                                value.usageAmount.value
                                            }
                                        />
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                </NTable>
                <div
                    style={
                        "display: flex; justify-content: center; margin-top: 16px"
                    }
                >
                    <NButton
                        strong
                        round
                        type={"tertiary"}
                        onClick={props.previous}
                    >
                        上一步
                    </NButton>
                    <NButton
                        strong
                        round
                        type={"primary"}
                        onClick={(e: MouseEvent) => {
                            e.preventDefault();
                            if (props.submit) {
                                props.submit(dataItemListRef.value);
                            }
                        }}
                    >
                        提交
                    </NButton>
                </div>
            </div>
        );
    },
});
