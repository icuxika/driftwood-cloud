import { DataTableColumn, NDropdown } from "naive-ui";
import { h } from "vue";
import { defineDropdownCRUDOptions } from "@/types/data";
import { U, D } from "@/types/data";

export interface RoleColumnAction {
	handleDropdownClick: (
		optionKey: string | number,
		rowData: object,
		rowIndex: number
	) => void;
}

export const defineRoleColumnList = (
	roleColumnAction?: RoleColumnAction
): DataTableColumn[] => {
	return [
		{
			title: "ID",
			key: "id",
			sorter: true,
			sortOrder: "ascend",
		},
		{
			title: "角色名称",
			key: "name",
			sorter: false,
			sortOrder: false,
		},
		{
			title: "角色",
			key: "role",
			sorter: false,
			sortOrder: false,
		},
		{
			title: "角色描述",
			key: "description",
			sorter: false,
			sortOrder: false,
		},
		{
			title: "创建时间",
			key: "createTime",
			sorter: false,
			sortOrder: false,
		},
		{
			title: "更新时间",
			key: "updateTime",
			sorter: false,
			sortOrder: false,
		},
		{
			title: "操作",
			key: "actions",
			sorter: false,
			sortOrder: false,
			render(rowData: object, rowIndex: number) {
				return h(
					NDropdown,
					{
						options: defineDropdownCRUDOptions(U | D),
						onSelect: (key: string | number) => {
							roleColumnAction?.handleDropdownClick(
								key,
								rowData,
								rowIndex
							);
						},
					},
					{
						default: () => "编辑",
					}
				);
			},
		},
	];
};
