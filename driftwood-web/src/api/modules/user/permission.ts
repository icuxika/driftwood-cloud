import AxiosInstance from "@/api/axios";
import { ApiData, BaseEntity, HasId } from "@/api";
import { AxiosResponse } from "axios";
import { isMockMode, mockResponse } from "@/api/mock";

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

type PermissionWithId = Permission & HasId;

type CreateService = (path: string) => {
    list: (
        permission: Partial<Permission>
    ) => Promise<AxiosResponse<ApiData<PermissionWithId[]>>>;

    getById: (
        id: PermissionWithId["id"]
    ) => Promise<AxiosResponse<ApiData<PermissionWithId>>>;

    save: (
        menu: Omit<Partial<Permission>, "id">
    ) => Promise<AxiosResponse<ApiData<PermissionWithId>>>;

    update: (
        menu: Partial<Permission> & HasId
    ) => Promise<AxiosResponse<ApiData<PermissionWithId>>>;

    deleteById: (
        id: PermissionWithId["id"]
    ) => Promise<AxiosResponse<ApiData<never>>>;
};

const createService: CreateService = (path: string) => {
    return {
        list(permission) {
            return AxiosInstance.get(path + "/list", { params: permission });
        },
        getById(id) {
            return AxiosInstance.get(path + "/" + id);
        },
        save(permission) {
            return AxiosInstance.post(path, permission);
        },
        update(permission) {
            return AxiosInstance.put(path, permission);
        },
        deleteById(id) {
            return AxiosInstance.delete(path + "/" + id);
        },
    };
};

const mockData: PermissionWithId[] = [
    {
        id: 1,
        name: "新增",
        authority: "user:user:add",
        type: 0,
        groupId: 4,
        description: "",
    },
    {
        id: 1,
        name: "新增",
        authority: "user:role:add",
        type: 0,
        groupId: 5,
        description: "",
    },
];

const errorMockData = {
    id: 1,
    name: "新增",
    authority: "user:role:add",
    type: 0,
    groupId: 5,
    description: "",
};

const createMockService: CreateService = (path: string) => {
    return {
        list(permission) {
            return mockResponse(mockData);
        },
        getById(id) {
            return mockResponse(
                mockData.find((p) => p.id === id) ?? errorMockData
            );
        },
        save(permission) {
            const newId = Math.max(...mockData.map((p) => p.id)) + 1;
            const newPermission = permission as PermissionWithId;
            newPermission.id = newId;
            mockData.push(newPermission);
            return mockResponse(newPermission);
        },
        update(permission) {
            const existPermission = mockData.find(
                (p) => p.id === permission.id
            );
            if (existPermission) {
                Object.keys(permission).forEach((key) => {
                    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
                    // @ts-ignore
                    existPermission[key] = permission[key];
                });
                return mockResponse(existPermission);
            }
            return mockResponse(errorMockData);
        },
        deleteById(id) {
            const index = mockData.findIndex(
                (permission) => permission.id === id
            );
            if (index != -1) {
                mockData.splice(index, 1);
            }
            return mockResponse("" as never);
        },
    };
};

const PATH = "/user/permission";

const permissionService = isMockMode
    ? createMockService(PATH)
    : createService(PATH);

export { permissionService };
export type { Permission, PermissionWithId };
