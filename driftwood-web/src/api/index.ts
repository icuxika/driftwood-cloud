/**
 * 服务器返回数据包装
 */
interface ApiData<T> {
	/**
	 * 状态码
	 */
	code: number;

	/**
	 * 数据
	 */
	data: T;

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

export { ApiData, Pageable, Page, HasId, BaseEntity, BindOneDTO };
