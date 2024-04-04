import {
    ApiData,
    ApiDataResponse,
    BaseEntity,
    BindOneDTO,
    HasId,
    Page,
    Pageable,
} from "@/api";
import AxiosInstance from "@/api/axios";

/**
 * 角色
 */
interface Role extends BaseEntity {
    name: string;
    role: string;
}

type RoleWithId = Role & HasId;

type CreateService = (path: string) => {
    page: (pageable: Partial<Pageable & Role>) => ApiDataResponse<Page<Role>>;

    getById: (id: RoleWithId["id"]) => ApiDataResponse<RoleWithId>;

    save: (role: Omit<Partial<Role>, "id">) => ApiDataResponse<never>;

    update: (role: Partial<Role> & HasId) => ApiDataResponse<never>;

    deleteById: (id: RoleWithId["id"]) => ApiDataResponse<never>;

    bindAuthorities: (bindOneDTO: BindOneDTO) => ApiDataResponse<never>;
};

const createService: CreateService = (path) => {
    return {
        page: (pageable: Partial<Pageable & Role>) => {
            return AxiosInstance.get<ApiData<Page<Role>>>(`${path}/page`, {
                params: pageable,
            });
        },

        getById: (id: RoleWithId["id"]) => {
            return AxiosInstance.get<ApiData<RoleWithId>>(`${path}/${id}`);
        },

        save: (role: Omit<Partial<Role>, "id">) => {
            return AxiosInstance.post<ApiData<never>>(`${path}`, role);
        },

        update: (role: Partial<Role> & HasId) => {
            return AxiosInstance.put<ApiData<never>>(`${path}`, role);
        },

        deleteById: (id: RoleWithId["id"]) => {
            return AxiosInstance.delete<ApiData<never>>(`${path}/${id}`);
        },

        bindAuthorities: (bindOneDTO: BindOneDTO) => {
            return AxiosInstance.post<ApiData<never>>(
                `${path}/bind-authorities`,
                bindOneDTO
            );
        },
    };
};

const roleService = createService("/user/role");

export { roleService };
export type { Role };
