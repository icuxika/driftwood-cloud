<template>
	<div>
		<h1>角色管理</h1>
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
	PaginationProps,
} from "naive-ui";
import { Role } from "@/api/modules/user/user";

const columnList: DataTableColumn[] = [
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

// 等价于 Array.apply(null, { length: 987 })，为了创建指定长度并且每个元素都被初始化的数组，否则map无法遍历操作
const roleData: Role[] = Array.apply(null, Array.from({ length: 987 })).map(
	(_, index) => {
		return {
			id: index,
			createTime: "2022-04-08",
			createUserId: 0,
			updateTime: "2022-04-08",
			updateUserId: 0,
			name: "u_" + index,
			role: "p_" + index,
			description: "12345678910",
		};
	}
);

const data = ref<Role[]>([]);
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
const rowKey = (rowData: Role) => {
	return rowData.id;
};

interface QueryData {
	pageCount: number;
	data: Role[];
	total: number;
}

const query = (
	page: number,
	pageSize = 10,
	order = "ascend",
	filterValues = []
): Promise<QueryData> => {
	return new Promise((resolve) => {
		const pagedData = roleData.slice(
			(page - 1) * pageSize,
			page * pageSize
		);
		const total = roleData.length;
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
