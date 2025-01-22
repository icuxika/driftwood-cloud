import {
    ApiDataResponse,
    BaseEntity,
    BindOneDTO,
    HasId,
    Page,
    Pageable,
} from "@/api";
import AxiosInstance from "@/api/axios";
import { Menu } from "@/api/modules/user/menu";
import { Permission } from "@/api/modules/user/permission";
import { Role } from "@/api/modules/user/role";
import { AxiosResponse } from "axios";

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
    getUserInfo: () => ApiDataResponse<UserInfoVO>;

    /**
     * 用户分页查询
     */
    page: <T extends User>(
        pageable: Partial<Pageable & T>
    ) => ApiDataResponse<Page<UserVO>>;

    /**
     * 根据id查询用户
     */
    getById: (id: UserWithId["id"]) => ApiDataResponse<User>;

    /**
     * 新增用户
     */
    save: (user: Omit<Partial<User>, "id">) => ApiDataResponse<never>;

    /**
     * 更新用户
     */
    update: (user: Partial<User> & HasId) => ApiDataResponse<never>;

    /**
     * 根据id删除用户
     */
    deleteById: (id: UserWithId["id"]) => ApiDataResponse<never>;

    /**
     * 为用户绑定角色
     */
    bindRoles: (bindOneDTO: BindOneDTO) => ApiDataResponse<never>;

    exportExcel: <T extends User>(user: Partial<T>) => Promise<AxiosResponse>;
};

const createService: CreateService = (path: string) => {
    return {
        getUserInfo() {
            return AxiosInstance.get(`${path}/getUserInfo`);
        },
        page(pageable) {
            return AxiosInstance.get(`${path}/page`, {
                timeout: 1000 * 10,
                params: pageable,
            });
        },
        getById(id) {
            return AxiosInstance.get(`${path}/${id}`);
        },
        save(user) {
            return AxiosInstance.post(path, user);
        },
        update(user) {
            return AxiosInstance.put(path, user);
        },
        deleteById(id) {
            return AxiosInstance.delete(`${path}/${id}`);
        },
        bindRoles(bindOneDTO) {
            return AxiosInstance.post(`${path}/bindRoles`, bindOneDTO);
        },
        exportExcel(user) {
            return AxiosInstance.get(`${path}/export`, {
                params: user,
                responseType: "blob",
            });
        },
    };
};

const userService = createService("/user/user");

export { userService };
export type { User, UserInfoVO, UserVO };
