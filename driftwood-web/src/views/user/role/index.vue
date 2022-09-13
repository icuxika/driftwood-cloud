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
import { onMounted, reactive, ref } from "vue";
import {
	DataTableBaseColumn,
	DataTableFilterState,
	DataTableSortState,
	PaginationProps,
} from "naive-ui";
import { defineRoleColumnList } from "@/views/user/role/data";
import { Page } from "@/api";
import { PartialPageable, usePage } from "@/hooks/use-page";
import { Role } from "@/api/modules/user/role";

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
const columns = ref(
	defineRoleColumnList({
		handleDropdownClick(
			optionKey: string | number,
			rowData: object,
			rowIndex: number
		) {
			console.log(optionKey);
			console.log((rowData as Role).name);
			console.log(rowIndex);
		},
	})
);

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

// 非数据表属性直接对应的参数
interface RoleQuery extends Role {}
interface RoleResult extends Role {}

const query = (pageable: PartialPageable<Role>): Promise<Page<Role>> => {
	return new Promise((resolve) => {
		const pagedData = roleData.slice(
			(pageable.page - 1) * pageable.size,
			pageable.page * pageable.size
		);
		const total = roleData.length;
		const pageCount = Math.ceil(total / pageable.size);
		setTimeout(() => {
			resolve({
				content: pagedData,
				totalPages: pageCount,
				totalElements: total,
				size: pageable.size,
				number: pageable.page,
			});
		}, 500);
	});
};

const { refreshPage } = usePage<RoleQuery, RoleResult>(
	query,
	data,
	loading,
	pagination
);

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
	refreshPage({
		size: pagination.pageSize,
		page: page,
	});
};

const handlePageSizeChange = (pageSize: number) => {
	refreshPage({
		size: pageSize,
		page: pagination.page,
	});
};

onMounted(() => {
	refreshPage({
		size: pagination.pageSize,
		page: pagination.page,
	});
});
</script>

<style lang="scss" scoped></style>
