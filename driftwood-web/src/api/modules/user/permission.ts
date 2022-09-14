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
};

const createService: CreateService = (path: string) => {
	return {
		list(permission) {
			return AxiosInstance.get(path + "/list", { params: permission });
		},
	};
};

const createMockService: CreateService = (path: string) => {
	return {
		list(permission) {
			return mockResponse([
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
			]);
		},
	};
};

const PATH = "/user/permission";

const permissionService = isMockMode
	? createMockService(PATH)
	: createService(PATH);

export { permissionService };
export type { Permission, PermissionWithId };
