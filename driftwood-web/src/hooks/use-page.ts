import { Ref, UnwrapNestedRefs } from "vue";
import { Page, Pageable } from "@/api";

export type PartialPageable<T> = Partial<Pageable & T> & {
	/**
	 * 第几页（从 1 开始）
	 */
	size: number;

	/**
	 * 每页条数
	 */
	page: number;
};

/**
 * 分页查询封装
 * @param query 分页查询
 * @param data 数据表当前页数据引用
 * @param loading 数据表加载状态引用
 * @param pagination 数据表分页参数引用
 */
export const usePage = <P, R>(
	query: (pageable: PartialPageable<P>) => Promise<Page<R>>,
	data: Ref<R[]>,
	loading: Ref<boolean>,
	pagination: UnwrapNestedRefs<{
		page: number;
		pageSize: number;
		pageCount: number;
		itemCount: number;
	}>
) => {
	// 加载分页查询结果
	const loadPage = (page: number, pageSize: number, pageResult: Page<R>) => {
		data.value = pageResult.content;
		pagination.page = page;
		pagination.pageSize = pageSize;
		pagination.pageCount = pageResult.totalPages;
		pagination.itemCount = pageResult.totalElements;
	};

	// 刷新分页
	const refreshPage = async (pageable: PartialPageable<P>) => {
		if (!loading.value) {
			loading.value = true;
		}
		try {
			const pageResult = await query(pageable);
			loadPage(pageable.page, pageable.size, pageResult);
		} catch (error) {
			console.error("[usePage]", error);
		} finally {
			loading.value = false;
		}
	};

	return {
		refreshPage: refreshPage,
	};
};
