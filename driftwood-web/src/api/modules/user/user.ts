import { AxiosResponse } from "axios";
import AxiosInstance from "@/api/axios";
import { ApiData, BaseEntity, BindOneDTO, HasId, Page, Pageable } from "@/api";

/**
 * 用户数据
 */
interface User extends BaseEntity {
	username: string;
	password: string;
	phone: string;
	nickname: string;
	isAccountNonExpired?: boolean;
	isAccountNonLocked?: boolean;
	isCredentialsNonExpired?: boolean;
	isEnabled?: boolean;
}

/**
 * 用户数据，包含角色、权限、菜单等信息
 */
interface UserVO {
	userId: number;
	nickname: string;
	roleList: Role[];
	permissionList: Permission[];
	menuList: Menu[];
}

/**
 * 角色
 */
interface Role extends BaseEntity {
	name: string;
	role: string;
}

/**
 * 权限
 */
interface Permission extends BaseEntity {
	name: string;
	authority: string;
	type: number;
	groupId: number;
	description: string;
}

/**
 * 菜单
 */
interface Menu extends BaseEntity {
	parentId: number;
	type: number;
	name: string;
	icon: string;
	path: string;
	sort: number;
	status: number;
}

type UserWithId = User & HasId;

type CreateService = (path: string) => {
	/**
	 * 获取登录用户信息
	 */
	getUserInfo: () => Promise<AxiosResponse<ApiData<UserVO>>>;

	/**
	 * 用户分页查询
	 */
	page: (
		pageable: Pageable | User
	) => Promise<AxiosResponse<ApiData<Page<User>>>>;

	/**
	 * 根据id查询用户
	 */
	getById: (id: User["id"]) => Promise<AxiosResponse<ApiData<User>>>;

	/**
	 * 新增用户
	 */
	save: (
		user: Omit<Partial<User>, "id">
	) => Promise<AxiosResponse<ApiData<never>>>;

	/**
	 * 更新用户
	 */
	update: (user: UserWithId) => Promise<AxiosResponse<ApiData<never>>>;

	/**
	 * 根据id删除用户
	 */
	deleteById: (id: User["id"]) => Promise<AxiosResponse<ApiData<never>>>;

	/**
	 * 为用户绑定角色
	 */
	bindRoles: (
		bindOneDTO: BindOneDTO
	) => Promise<AxiosResponse<ApiData<never>>>;
};

const createService: CreateService = (path: string) => {
	return {
		getUserInfo() {
			return AxiosInstance.get<ApiData<UserVO>>(path + "/getUserInfo");
		},
		page(pageable: Pageable | User) {
			return AxiosInstance.get<ApiData<Page<User>>>(path + "/page", {
				params: pageable,
			});
		},
		getById(id: User["id"]) {
			return AxiosInstance.get<ApiData<User>>(path + "/" + id);
		},
		save(user: Omit<Partial<User>, "id">) {
			return AxiosInstance.post<ApiData<never>>(path, user);
		},
		update(user: UserWithId) {
			return AxiosInstance.put<ApiData<never>>(path, user);
		},
		deleteById(id: User["id"]) {
			return AxiosInstance.delete<ApiData<never>>(path + "/" + id);
		},
		bindRoles(bindOneDTO: BindOneDTO) {
			return AxiosInstance.post<ApiData<never>>(
				path + "/bindRoles",
				bindOneDTO
			);
		},
	};
};

const userService = createService("/user/user");

export { userService, User, Role };
