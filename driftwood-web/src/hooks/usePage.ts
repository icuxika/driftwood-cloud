import { Ref, UnwrapNestedRefs } from "vue";
import { Page, Pageable } from "@/api";

/**
 * 分页查询封装
 * @param query 分页查询
 * @param data 数据表当前页数据引用
 * @param loading 数据表加载状态引用
 * @param pagination 数据表分页参数引用
 */
export const usePage = <T>(
	query: (
		pageable: Partial<Pageable & T> & {
			size: number;
			page: number;
		}
	) => Promise<Page<T>>,
	data: Ref<T[]>,
	loading: Ref<boolean>,
	pagination: UnwrapNestedRefs<{
		page: number;
		pageSize: number;
		pageCount: number;
		itemCount: number;
	}>
) => {
	// 加载分页查询结果
	const loadPage = (page: number, pageSize: number, pageResult: Page<T>) => {
		data.value = pageResult.content;
		pagination.page = page;
		pagination.pageSize = pageSize;
		pagination.pageCount = pageResult.totalPages;
		pagination.itemCount = pageResult.totalElements;
	};

	// 刷新分页
	const refreshPage = <U extends T>(
		pageable: Partial<Pageable & U> & {
			size: number;
			page: number;
		}
	) => {
		if (!loading.value) {
			loading.value = true;
		}
		query(pageable)
			.then((pageResult) => {
				loadPage(pageable.page, pageable.size, pageResult);
				loading.value = false;
			})
			.catch(() => {
				loading.value = false;
			});
	};

	return {
		refreshPage: refreshPage,
	};
};
