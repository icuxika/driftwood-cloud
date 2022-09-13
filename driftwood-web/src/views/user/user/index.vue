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
import { onMounted, reactive, ref } from "vue";
import {
	DataTableBaseColumn,
	DataTableFilterState,
	DataTableSortState,
	PaginationProps,
} from "naive-ui";
import { User } from "@/api/modules/user/user";
import { ApiData, NoNullReject, Page } from "@/api";
import { defineUserColumnList } from "@/views/user/user/data";
import { PartialPageable, usePage } from "@/hooks/use-page";
import { useUserStore } from "@/store/pinia/user/user";

const userStore = useUserStore();

const data = ref<User[]>([]);
const loading = ref(true);
const columns = ref(
	defineUserColumnList({
		handleDropdownClick(
			optionKey: string | number,
			rowData: object,
			rowIndex: number
		) {
			console.log(optionKey);
			console.log((rowData as User).nickname);
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

const rowKey = (rowData: User) => {
	return rowData.id;
};

// 非数据表属性直接对应的参数
interface UserQuery extends User {}
interface UserResult extends User {}

const query = async (pageable: PartialPageable<User>): Promise<Page<User>> => {
	const userPage = await userStore.page({
		...pageable, // ...pageable应放在首位，从而后面可以覆盖前面的
		sort: "id,desc",
		page: pageable.page - 1,
		size: pageable.size,
	});
	if (userPage) {
		return {
			content: userPage.content,
			totalPages: userPage.totalPages,
			totalElements: userPage.totalElements,
			size: userPage.size,
			number: userPage.number,
		};
	}
	return NoNullReject();
};

const { refreshPage } = usePage<UserQuery, UserResult>(
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
