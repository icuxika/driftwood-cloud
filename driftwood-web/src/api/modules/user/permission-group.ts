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

	getById: (
		id: PermissionGroupWithId["id"]
	) => Promise<AxiosResponse<ApiData<PermissionGroupWithId>>>;

	save: (
		menu: Omit<Partial<PermissionGroup>, "id">
	) => Promise<AxiosResponse<ApiData<PermissionGroupWithId>>>;

	update: (
		menu: Partial<PermissionGroup> & HasId
	) => Promise<AxiosResponse<ApiData<PermissionGroupWithId>>>;

	deleteById: (
		id: PermissionGroupWithId["id"]
	) => Promise<AxiosResponse<ApiData<never>>>;
};

const createService: CreateService = (path: string) => {
	return {
		list(permissionGroup) {
			return AxiosInstance.get(path + "/list", {
				params: permissionGroup,
			});
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

const mockData: PermissionGroupWithId[] = [
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
];

const errorMockData = {
	id: 7,
	name: "菜单",
	parentId: 2,
	description: "",
};

const createMockService: CreateService = (path: string) => {
	return {
		list(permissionGroup) {
			return mockResponse(mockData);
		},
		getById(id) {
			return mockResponse(
				mockData.find((p) => p.id === id) ?? errorMockData
			);
		},
		save(permissionGroup) {
			const newId = Math.max(...mockData.map((p) => p.id)) + 1;
			const newPermissionGroup = permissionGroup as PermissionGroupWithId;
			newPermissionGroup.id = newId;
			mockData.push(newPermissionGroup);
			return mockResponse(newPermissionGroup);
		},
		update(permissionGroup) {
			const existPermissionGroup = mockData.find(
				(p) => p.id === permissionGroup.id
			);
			if (existPermissionGroup) {
				Object.keys(permissionGroup).forEach((key) => {
					// eslint-disable-next-line @typescript-eslint/ban-ts-comment
					// @ts-ignore
					existPermissionGroup[key] = permissionGroup[key];
				});
				return mockResponse(existPermissionGroup);
			}
			return mockResponse(errorMockData);
		},
		deleteById(id) {
			const index = mockData.findIndex(
				(permissionGroup) => permissionGroup.id === id
			);
			if (index != -1) {
				mockData.splice(index, 1);
			}
			return mockResponse("" as never);
		},
	};
};

const PATH = "/user/permissionGroup";

const permissionGroupService = isMockMode
	? createMockService(PATH)
	: createService(PATH);

export { permissionGroupService };
export type { PermissionGroup, PermissionGroupWithId };
