<template>
	<div>
		<h1>用户管理</h1>
		<n-data-table
			remote
			striped
			:columns="columns"
			:data="data"
			:loading="loading"
			:pagination="pagination"
			:row-key="rowKey"
			@update:sorter="handleSorterChange"
			@update:filters="handleFiltersChange"
			@update:page="handlePageChange"
			@update:page-size="handlePageSizeChange"
		/>
	</div>
</template>

<script setup lang="ts">
import { h, onMounted, reactive, ref } from "vue";
import {
	DataTableBaseColumn,
	DataTableColumn,
	DataTableFilterState,
	DataTableSortState,
	NDropdown,
	NTag,
	PaginationProps,
} from "naive-ui";
import { User } from "../../api/modules/user/user";

const columnList: DataTableColumn[] = [
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

// 等价于 Array.apply(null, { length: 987 })，为了创建指定长度并且每个元素都被初始化的数组，否则map无法遍历操作
const userData: User[] = Array.apply(null, Array.from({ length: 987 })).map(
	(_, index) => {
		return {
			id: index,
			createTime: "2022-04-08",
			createUserId: 0,
			updateTime: "2022-04-08",
			updateUserId: 0,
			username: "u_" + index,
			password: "p_" + index,
			phone: "12345678910",
			nickname: "icuxika",
			isAccountNonExpired: true,
			isAccountNonLocked: true,
			isCredentialsNonExpired: true,
			isEnabled: true,
		};
	}
);

const data = ref<User[]>([]);
const loading = ref(true);
const columns = ref(columnList);
const pagination = reactive({
	page: 1,
	pageSize: 10,
	showSizePicker: true,
	pageSizes: [10, 15, 20],
	pageCount: 0,
	itemCount: 0,
	prefix({ itemCount }: PaginationProps) {
		return `总数：${itemCount}`;
	},
});
const rowKey = (rowData: User) => {
	return rowData.id;
};

interface QueryData {
	pageCount: number;
	data: User[];
	total: number;
}

const query = (
	page: number,
	pageSize = 10,
	order = "ascend",
	filterValues = []
): Promise<QueryData> => {
	return new Promise((resolve) => {
		const pagedData = userData.slice(
			(page - 1) * pageSize,
			page * pageSize
		);
		const total = userData.length;
		const pageCount = Math.ceil(total / pageSize);
		setTimeout(() => {
			resolve({
				pageCount,
				data: pagedData,
				total,
			});
		}, 500);
	});
};

const handleSorterChange = (options: DataTableSortState) => {
	console.log("sort");
	console.log(JSON.stringify(options));
	console.log(
		options.columnKey + "," + (options.order === "ascend" ? "asc" : "desc")
	);
};
const handleFiltersChange = (
	filters: DataTableFilterState,
	initiatorColumn: DataTableBaseColumn
) => {
	console.log("filters");
	console.log(JSON.stringify(filters));
	console.log(JSON.stringify(initiatorColumn));
};
const handlePageChange = (page: number) => {
	if (!loading.value) {
		loading.value = true;
		query(page, pagination.pageSize).then((queryData) => {
			data.value = queryData.data;
			pagination.page = page;
			pagination.pageCount = queryData.pageCount;
			pagination.itemCount = queryData.total;
			loading.value = false;
		});
	}
};
const handlePageSizeChange = (pageSize: number) => {
	if (!loading.value) {
		loading.value = true;
		query(pagination.page, pageSize).then((queryData) => {
			data.value = queryData.data;
			pagination.pageSize = pageSize;
			pagination.pageCount = queryData.pageCount;
			pagination.itemCount = queryData.total;
			loading.value = false;
		});
	}
};

onMounted(() => {
	query(pagination.page, pagination.pageSize).then((queryData) => {
		data.value = queryData.data;
		pagination.pageCount = queryData.pageCount;
		pagination.itemCount = queryData.total;
		loading.value = false;
	});
});
</script>

<style lang="scss" scoped></style>
