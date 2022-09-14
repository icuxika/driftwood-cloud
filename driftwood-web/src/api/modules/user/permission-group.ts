import AxiosInstance from "@/api/axios";
import { ApiData, BaseEntity, HasId } from "@/api";
import { AxiosResponse } from "axios";
import { mockResponse, isMockMode } from "@/api/mock";

interface PermissionGroup extends BaseEntity {
	name: string;
	parentId: number;
	description: string;
}

type PermissionGroupWithId = PermissionGroup & HasId;

type CreateService = (path: string) => {
	list: (
		permissionGroup: Partial<PermissionGroup>
	) => Promise<AxiosResponse<ApiData<PermissionGroupWithId[]>>>;
};

const createService: CreateService = (path: string) => {
	return {
		list(permissionGroup) {
			return AxiosInstance.get(path + "/list", {
				params: permissionGroup,
			});
		},
	};
};

const createMockService: CreateService = (path: string) => {
	return {
		list(permissionGroup) {
			return mockResponse([
				{
					id: 1,
					name: "授权服务",
					parentId: 0,
					description: "",
				},
				{
					id: 2,
					name: "用户服务",
					parentId: 0,
					description: "",
				},
				{
					id: 3,
					name: "客户端",
					parentId: 1,
					description: "",
				},
				{
					id: 4,
					name: "用户",
					parentId: 2,
					description: "",
				},
				{
					id: 5,
					name: "角色",
					parentId: 2,
					description: "",
				},
				{
					id: 6,
					name: "权限",
					parentId: 2,
					description: "",
				},
				{
					id: 7,
					name: "菜单",
					parentId: 2,
					description: "",
				},
			]);
		},
	};
};

const PATH = "/user/permissionGroup";

const permissionGroupService = isMockMode
	? createMockService(PATH)
	: createService(PATH);

export { permissionGroupService };
export type { PermissionGroup, PermissionGroupWithId };
