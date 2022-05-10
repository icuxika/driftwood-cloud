import { DataTableColumn, NDropdown } from "naive-ui";
import { h } from "vue";

export const roleColumnList: DataTableColumn[] = [
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
					options: [
						{
							label: "编辑",
							key: "edit",
						},
						{
							label: "删除",
							key: "delete",
						},
					],
					onSelect: (key: string | number) => {
						console.log(key);
					},
				},
				{
					default: () => "编辑",
				}
			);
		},
	},
];
