<template>
    <div>
        <h1>菜单管理</h1>
        <n-tree
            block-line
            cascade
            checkable
            draggable
            expand-on-click
            :data="menuStore.menuData"
            :node-props="nodeProps"
            @update:checked-keys="updateCheckedKeys"
            @drop="handleDrop"
            @dragstart="handleDragStart"
            @dragenter="handleDragEnter"
        />
        <n-dropdown
            trigger="manual"
            placement="bottom-start"
            :show="showDropdown"
            :options="options"
            :x="contextMenuX"
            :y="contextMenuY"
            @select="handleSelect"
            @clickoutside="handleClickOutSide"
        />
        <n-modal v-model:show="showMenuModal">
            <n-card
                style="width: 600px"
                :title="menuModalEditModeIsUpdate ? '编辑菜单' : '新增菜单'"
                :bordered="false"
                size="huge"
                role="dialog"
                aria-modal="true"
            >
                <n-form
                    :model="menuFormModelRef"
                    label-placement="left"
                    :label-width="80"
                >
                    <n-form-item label="菜单类型">
                        <n-select
                            v-model:value="menuFormModelRef.type"
                            :options="menuTypeDict"
                        />
                    </n-form-item>
                    <n-form-item label="父菜单">
                        <n-select
                            v-model:value="menuFormModelRef.parentId"
                            :options="menuStore.menuDict"
                            :disabled="!menuModalEditModeIsUpdate"
                        />
                    </n-form-item>
                    <n-form-item label="名称">
                        <n-input v-model:value="menuFormModelRef.name" />
                    </n-form-item>
                    <n-form-item label="图标">
                        <n-input v-model:value="menuFormModelRef.icon">
                            <template #suffix>
                                <n-button
                                    strong
                                    circle
                                    secondary
                                    type="info"
                                    @click="chooseIcon"
                                >
                                    <template #icon>
                                        <n-icon><search-icon /></n-icon>
                                    </template>
                                </n-button>
                            </template>
                        </n-input>
                    </n-form-item>
                    <n-form-item label="路径">
                        <n-input v-model:value="menuFormModelRef.path" />
                    </n-form-item>
                </n-form>
                <template #footer>
                    <n-space justify="end">
                        <n-button
                            type="success"
                            @click="handleSaveOrUpdateMenu"
                        >
                            确定</n-button
                        >
                        <n-button type="warning" @click="hideMenuEditModal">
                            取消</n-button
                        >
                    </n-space>
                </template>
            </n-card>
        </n-modal>
        <n-modal v-model:show="showIconChooseModal">
            <n-card
                style="width: 600px"
                title="选择图标"
                :bordered="false"
                size="huge"
                role="dialog"
                aria-modal="true"
            >
                <template #header-extra>
                    <component :is="iconComponent" />
                </template>
                <n-select
                    v-model:value="iconSelectedValue"
                    filterable
                    placeholder="选择图标"
                    :options="iconOptions"
                    :render-label="iconRenderLabel"
                    @update:value="handleIconUpdate"
                />
                <n-button
                    text
                    tag="a"
                    href="https://www.xicons.org/#/zh-CN"
                    target="_blank"
                    type="primary"
                >
                    在https://www.xicons.org/#/zh-CN中选择ionicons5分类进行搜索
                </n-button>
                <template #footer>
                    <n-space justify="end">
                        <n-button type="success" @click="handleIconChoose">
                            确定</n-button
                        >
                        <n-button
                            type="warning"
                            @click="showIconChooseModal = false"
                        >
                            取消</n-button
                        >
                    </n-space>
                </template>
            </n-card>
        </n-modal>
    </div>
</template>

<script setup lang="ts">
import { useIcon } from "@/hooks/use-icon";
import { useTree } from "@/hooks/use-tree";
import { useMenuStore } from "@/store/user/menu";
import {
    MenuFormModel,
    defaultMenuFormModel,
    menuFormModel,
} from "@/views/user/menu/data";
import { Search as SearchIcon } from "@vicons/ionicons5";
import {
    DropdownOption,
    NIcon,
    SelectOption,
    TreeDropInfo,
    TreeOption,
    useDialog,
    useMessage,
} from "naive-ui";
import { VNodeChild, h, onMounted, ref } from "vue";

defineOptions({
    name: "Menu0",
});

const message = useMessage();
const dialog = useDialog();
const menuStore = useMenuStore();
const { findSiblingsAndIndex, getParentTreeOption } = useTree();
const { loadIonicons5 } = useIcon();

const updateCheckedKeys = (checkedKeys: string[]) => {
    console.log(checkedKeys);
};

const handleDragStart = (data: { node: TreeOption; event: DragEvent }) => {};
const handleDragEnter = (data: { node: TreeOption; event: DragEvent }) => {};
const handleDrop = ({ node, dragNode, dropPosition }: TreeDropInfo) => {
    console.log("目标位置的节点：", JSON.stringify(node));
    console.log("被拖拽的节点：", JSON.stringify(dragNode));
    console.log(
        "被拖拽的节点位于目标位置节点的位置",
        JSON.stringify(dropPosition)
    );
    if ((dragNode.type as number) < (node.type as number)) {
        message.warning("不能高级别节点挂载到低级别节点下");
        return;
    }
    const [dragNodeSiblings, dragNodeIndex] = findSiblingsAndIndex(
        dragNode,
        menuStore.menuData
    );
    if (dragNodeSiblings === null || dragNodeIndex === null) {
        message.warning("当前节点数据异常，无法完成操作");
        return;
    }
    console.log("被拖拽节点同级的所有节点: ", JSON.stringify(dragNodeSiblings));
    console.log("被拖拽节点在同级所有节点的索引: ", dragNodeIndex);
    // 目标节点是分组
    dragNodeSiblings.splice(dragNodeIndex, 1);
    if (dropPosition === "inside") {
        if (node.children) {
            node.children.unshift(dragNode);
        } else {
            node.children = [dragNode];
        }
    } else if (dropPosition === "before") {
        const [nodeSiblings, nodeIndex] = findSiblingsAndIndex(
            node,
            menuStore.menuData
        );
        if (nodeSiblings === null || nodeIndex === null) {
            return;
        }
        nodeSiblings.splice(nodeIndex, 0, dragNode);
    } else if (dropPosition === "after") {
        const [nodeSiblings, nodeIndex] = findSiblingsAndIndex(
            node,
            menuStore.menuData
        );
        if (nodeSiblings === null || nodeIndex === null) {
            return;
        }
        nodeSiblings.splice(nodeIndex + 1, 0, dragNode);
    }
    menuStore.menuData = Array.from(menuStore.menuData);
    console.log("tree", JSON.stringify(menuStore.menuData));
};

// 树节点右键菜单
const showDropdown = ref(false);
const handleSelect = () => (showDropdown.value = false);
const handleClickOutSide = () => (showDropdown.value = false);
// 右键菜单选项
const options = ref<DropdownOption[]>([]);
const contextMenuX = ref(0);
const contextMenuY = ref(0);
const nodeProps = ({ option }: { option: TreeOption }) => {
    return {
        onContextmenu(e: MouseEvent): void {
            options.value = [
                ...(option.type !== 4
                    ? [
                          {
                              label: "新增",
                              key: "add->" + option.key,
                              props: {
                                  onClick: () => {
                                      showMenuModal.value = true;
                                      menuModalEditModeIsUpdate.value = false;
                                      Object.assign(
                                          menuFormModel,
                                          defaultMenuFormModel
                                      );
                                      menuFormModel.parentId =
                                          option.key as number;
                                  },
                              },
                          },
                      ]
                    : []),
                {
                    label: "编辑",
                    key: "edit->" + option.key,
                    props: {
                        onClick: () => {
                            showMenuModal.value = true;
                            menuModalEditModeIsUpdate.value = true;
                            let cacheMenu = menuStore.getCacheMenuById(
                                option.key as number
                            );
                            if (cacheMenu) {
                                Object.assign(menuFormModel, cacheMenu);
                            }
                        },
                    },
                },
                {
                    label: "删除",
                    key: "delete->" + option.key,
                    props: {
                        onClick: () => {
                            const d = dialog.warning({
                                title: "删除菜单：" + option.label,
                                content: "你确定？",
                                positiveText: "确定",
                                negativeText: "不确定",
                                onPositiveClick: () => {
                                    d.loading = true;
                                    return handleDeleteMenu(
                                        option.key as number
                                    );
                                },
                            });
                        },
                    },
                },
            ];
            showDropdown.value = true;
            contextMenuX.value = e.clientX;
            contextMenuY.value = e.clientY;
            e.preventDefault();
        },
    };
};

// 是不是编辑模式
const menuModalEditModeIsUpdate = ref(true);
// 是否展示菜单编辑模态框
const showMenuModal = ref(false);
const menuFormModelRef = ref<MenuFormModel>(menuFormModel);
// 菜单类型
const menuTypeDict = [
    {
        label: "菜单-应用",
        value: 1,
    },
    {
        label: "菜单-服务",
        value: 2,
    },
    {
        label: "菜单-URL",
        value: 3,
    },
    {
        label: "按钮",
        value: 4,
    },
];

// 保存或更新菜单
const handleSaveOrUpdateMenu = async () => {
    const isUpdate = menuModalEditModeIsUpdate.value;
    if (isUpdate) {
        await menuStore.updateMenu(menuFormModel);
        message.success("更新菜单成功");
    } else {
        await menuStore.saveMenu(menuFormModel);
        message.success("新增菜单成功");
    }
    hideMenuEditModal();
};

// 删除菜单
const handleDeleteMenu = async (id: number) => {
    await menuStore.deleteMenu(id);
    message.success("删除菜单成功");
};

// 隐藏菜单编辑模态框
const hideMenuEditModal = () => {
    showMenuModal.value = false;
};

// 图标选择模态框
const showIconChooseModal = ref(false);
const chooseIcon = async () => {
    iconSelectedValue.value = menuFormModelRef.value.icon;
    showIconChooseModal.value = true;
};
const iconSelectedValue = ref<string>("");
const iconOptions = ref<SelectOption[]>([]);
const handleIconUpdate = async (value: string, option: SelectOption) => {
    let selectedIcon = cacheIcons.filter(
        (icon) => (icon as any).name === value
    );
    iconComponent.value = h(
        NIcon,
        { size: 32 },
        { default: () => h(selectedIcon[0]) }
    );
};
const iconComponent = ref<object | null>(null);
const iconRenderLabel = (option: SelectOption): VNodeChild => {
    let selectedIcon = cacheIcons.filter(
        (icon) => (icon as any).name === option.label
    );
    return [
        h(
            NIcon,
            {
                style: {
                    verticalAlign: "-0.15em",
                    marginRight: "4px",
                },
            },
            { default: () => h(selectedIcon[0]) }
        ),
        option.label as string,
    ];
};
const handleIconChoose = async () => {
    menuFormModelRef.value.icon = iconSelectedValue.value;
    showIconChooseModal.value = false;
};

// 刷新菜单
const refreshMenu = async () => {
    let menuList = await menuStore.listMenu();
    if (menuList) {
        menuStore.initCacheMenu(menuList);
        await menuStore.refreshMenu();
    }
};

// 缓存图标
const cacheIcons: object[] = [];
// 初始化图表数据
const loadIconOptions = async () => {
    const icons = await loadIonicons5();
    let options: SelectOption[] = [];
    icons.forEach((icon) => {
        cacheIcons.push(icon);
        options.push({
            label: (icon as any).name,
            value: (icon as any).name,
        });
    });
    iconOptions.value = options;
};

const initialize = async () => {
    await refreshMenu();
    await loadIconOptions();
};
onMounted(initialize);
</script>

<style lang="scss" scoped></style>
