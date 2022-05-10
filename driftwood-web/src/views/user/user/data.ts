import { DataTableColumn, NDropdown, NTag } from "naive-ui";
import { h } from "vue";
import { User } from "@/api/modules/user/user";

export const userColumnList: DataTableColumn[] = [
	{
		title: "ID",
		key: "id",
		sorter: true,
		sortOrder: "ascend",
	},
	{
		title: "用户名",
		key: "username",
		sorter: false,
		sortOrder: false,
	},
	{
		title: "密码",
		key: "password",
		sorter: false,
		sortOrder: false,
	},
	{
		title: "手机号",
		key: "phone",
		sorter: false,
		sortOrder: false,
	},
	{
		title: "昵称",
		key: "nickname",
		sorter: false,
		sortOrder: false,
	},
	{
		title: "账户是否没有过期",
		key: "isAccountNonExpired",
		sorter: false,
		sortOrder: false,
		render(rowData: object, rowIndex: number) {
			return h(
				NTag,
				{
					type: "info",
				},
				{
					default: () =>
						(rowData as User).isAccountNonExpired ? "是" : "否",
				}
			);
		},
	},
	{
		title: "账户是否没有冻结",
		key: "isAccountNonLocked",
		sorter: false,
		sortOrder: false,
		render(rowData: object, rowIndex: number) {
			return h(
				NTag,
				{
					type: "info",
				},
				{
					default: () =>
						(rowData as User).isAccountNonLocked ? "是" : "否",
				}
			);
		},
	},
	{
		title: "账户凭证是否没有过期",
		key: "isCredentialsNonExpired",
		sorter: false,
		sortOrder: false,
		render(rowData: object, rowIndex: number) {
			return h(
				NTag,
				{
					type: "info",
				},
				{
					default: () =>
						(rowData as User).isCredentialsNonExpired ? "是" : "否",
				}
			);
		},
	},
	{
		title: "账户是否启用",
		key: "isEnabled",
		sorter: false,
		sortOrder: false,
		render(rowData: object, rowIndex: number) {
			return h(
				NTag,
				{
					type: "info",
				},
				{
					default: () => ((rowData as User).isEnabled ? "是" : "否"),
				}
			);
		},
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
