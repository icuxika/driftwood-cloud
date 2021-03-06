import { AxiosResponse } from "axios";
import AxiosInstance from "@/api/axios";
import { ApiData, BaseEntity, BindOneDTO, HasId, Page, Pageable } from "@/api";
import { Role } from "@/api/modules/user/role";
import { Permission } from "@/api/modules/user/permission";
import { Menu } from "@/api/modules/user/menu";

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
 * 用户资料
 */
interface UserProfile extends BaseEntity {
	userId: number;
	avatar: string;
	nation: string;
	province: string;
	city: string;
	district: string;
	street: string;
	streetNumber: string;
	lastRemoteAddress: string;
	remoteAddress: string;
	birthday: string;
	gender: number;
	signature: string;
}

/**
 * 用户数据，包含角色、权限、菜单等信息
 */
interface UserInfoVO {
	id: number;
	nickname: string;
	userProfile?: UserProfile;
	roleList: Role[];
	permissionList: Permission[];
	menuList: Menu[];
}

/**
 * 用户详细数据
 */
interface UserVO extends User {
	userProfile?: UserProfile;
}

type UserWithId = User & HasId;

type CreateService = (path: string) => {
	/**
	 * 获取登录用户信息
	 */
	getUserInfo: () => Promise<AxiosResponse<ApiData<UserInfoVO>>>;

	/**
	 * 用户分页查询
	 */
	page: <T extends User>(
		pageable: Partial<Pageable & T>
	) => Promise<AxiosResponse<ApiData<Page<UserVO>>>>;

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
			return AxiosInstance.get<ApiData<UserInfoVO>>(
				path + "/getUserInfo"
			);
		},
		page<T extends User>(pageable: Partial<Pageable & T>) {
			return AxiosInstance.get<ApiData<Page<UserVO>>>(path + "/page", {
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

export { userService, User, UserInfoVO, UserVO };
