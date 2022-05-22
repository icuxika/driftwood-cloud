/**
 * 服务器返回数据包装
 */
import { AxiosResponse } from "axios";

interface ApiData<T> {
	/**
	 * 状态码
	 */
	code: number;

	/**
	 * 数据
	 */
	data: T | null;

	/**
	 * 消息
	 */
	msg: string;

	/**
	 * 业务状态码 [10000, 20000) 代表请求成功
	 * 应确保后端报错返回的状态码大于20000
	 */
	success: boolean;
}

/**
 * 分页参数
 */
interface Pageable {
	size: number;
	page: number;
	sort?: string;
}

/**
 * 分页返回包装
 */
interface Page<T> {
	content: T[];
	totalPages: number;
	totalElements: number;
	size: number;
	number: number;
}

/**
 * 树结构数据返回包装
 */
interface TreeNode<T> {
	id: number;
	parentId: number;
	data: T;
	children: TreeNode<T>[];
}

/**
 * 用于属于类 & 组合使必须提供id属性
 */
interface HasId {
	id: number;
}

/**
 * 业务数据基本机构
 */
interface BaseEntity {
	/**
	 * 主键id
	 */
	id?: number;

	/**
	 * 创建时间
	 */
	createTime?: string;

	/**
	 * 创建用户id
	 */
	createUserId?: number;

	/**
	 * 更新时间
	 */
	updateTime?: string;

	/**
	 * 更新用户id
	 */
	updateUserId?: number;
}

/**
 * 创建关联关系的DTO 1 -> n
 */
interface BindOneDTO {
	/**
	 * 被关联的业务主键id
	 */
	id: number;

	/**
	 * 关联的业务主键集合
	 */
	idList: number[];
}

/**
 * 对axios请求返回结果进行解析
 * @param block 业务请求（返回结果满足ApiData<T>）
 */
const resolveAxiosResult = async <T>(
	block: () => Promise<AxiosResponse<ApiData<T>>>
): Promise<T | null> => {
	let apiData: ApiData<T>;
	try {
		const servicePromise = await block();
		apiData = servicePromise.data;
	} catch (error) {
		// 此处错误已在axios响应拦截器处理过
		return Promise.reject("系统异常：" + JSON.stringify(error));
	}
	if (apiData.success) {
		return apiData.data;
	} else {
		// 业务异常
		const errorMsg = "错误信息：[" + apiData.code + "]" + apiData.msg;
		window.$message.error(errorMsg, {
			duration: 2500,
		});
		return Promise.reject(errorMsg);
	}
};

/**
 * 类似分页查询，一般情况下ApiData的data不会为null
 * 注意：不能定义 const NoNullReject = Promise.reject("不应出现此错误")
 * 定义这样一个const来使用时会产生莫名的未捕获错误，因此通过返回一个函数来使用
 */
const NoNullReject = () => Promise.reject("不应出现的数据为空错误");

/**
 * 判断目标是否是 undefined
 */
const IsUndefined = <T>(target: T | null): boolean =>
	typeof target === "undefined";

export {
	ApiData,
	Pageable,
	Page,
	HasId,
	BaseEntity,
	BindOneDTO,
	resolveAxiosResult,
	NoNullReject,
};
