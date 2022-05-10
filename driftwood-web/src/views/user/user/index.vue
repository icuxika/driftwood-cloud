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
import { useStore } from "@/store";
import { ApiData, Page } from "@/api";
import { userColumnList } from "@/views/user/user/data";

const store = useStore();

const data = ref<User[]>([]);
const loading = ref(true);
const columns = ref(userColumnList);
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
		store
			.dispatch("user/page", {
				sort: "id,desc",
				page: page - 1,
				size: pageSize,
			})
			.then((apiData: ApiData<Page<User>>) => {
				setTimeout(() => {
					resolve({
						pageCount: apiData.data.totalPages,
						data: apiData.data.content,
						total: apiData.data.totalElements,
					});
				}, 500);
			})
			.catch((error) => {
				console.log(error);
			});
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
