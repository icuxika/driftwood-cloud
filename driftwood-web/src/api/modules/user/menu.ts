import AxiosInstance from "@/api/axios";
import { ApiData, BaseEntity, BindOneDTO, HasId } from "@/api";
import { AxiosResponse } from "axios";
import { isMockMode, mockResponse } from "@/api/mock";

/**
 * 菜单
 */
interface Menu extends BaseEntity {
	parentId: number;
	type: number;
	name: string;
	icon: string;
	path: string;
	sequence: number;
	status: number;
}

type MenuWithId = Menu & HasId;

type CreateService = (path: string) => {
	list: (
		menu: Partial<Menu>
	) => Promise<AxiosResponse<ApiData<MenuWithId[]>>>;

	getById: (
		id: MenuWithId["id"]
	) => Promise<AxiosResponse<ApiData<MenuWithId>>>;

	save: (
		menu: Omit<Partial<Menu>, "id">
	) => Promise<AxiosResponse<ApiData<MenuWithId>>>;

	update: (
		menu: Partial<Menu> & HasId
	) => Promise<AxiosResponse<ApiData<MenuWithId>>>;

	deleteById: (
		id: MenuWithId["id"]
	) => Promise<AxiosResponse<ApiData<never>>>;

	bindAuthorities: (
		bindOneDTO: BindOneDTO
	) => Promise<AxiosResponse<ApiData<never>>>;
};

const createService: CreateService = (path: string) => {
	return {
		list(menu) {
			return AxiosInstance.get(path + "/list", { params: menu });
		},
		getById(id) {
			return AxiosInstance.get(path + "/" + id);
		},
		save(menu) {
			return AxiosInstance.post(path, menu);
		},
		update(menu) {
			return AxiosInstance.put(path, menu);
		},
		deleteById(id) {
			return AxiosInstance.delete(path + "/" + id);
		},
		bindAuthorities(bindOneDTO) {
			return AxiosInstance.post(path + "/bindAuthorities", bindOneDTO);
		},
	};
};

const mockData: MenuWithId[] = [
	{
		id: 2,
		createTime: "2022-08-31 13:59:37",
		createUserId: 2,
		updateTime: "2022-08-31 13:59:37",
		updateUserId: 2,
		parentId: 0,
		type: 1,
		name: "系统",
		icon: "BookOutline",
		path: "/",
		sequence: 0,
		status: 0,
	},
	{
		id: 3,
		createTime: "2022-09-01 12:13:38",
		createUserId: 2,
		updateTime: "2022-09-01 12:13:38",
		updateUserId: 2,
		parentId: 2,
		type: 2,
		name: "授权服务",
		icon: "BookOutline",
		path: "/",
		sequence: 0,
		status: 0,
	},
	{
		id: 4,
		createTime: "2022-09-01 12:14:02",
		createUserId: 2,
		updateTime: "2022-09-01 12:14:02",
		updateUserId: 2,
		parentId: 2,
		type: 2,
		name: "用户服务",
		icon: "BookOutline",
		path: "/",
		sequence: 0,
		status: 0,
	},
	{
		id: 5,
		createTime: "2022-09-01 12:14:48",
		createUserId: 2,
		updateTime: "2022-09-01 12:14:48",
		updateUserId: 2,
		parentId: 3,
		type: 3,
		name: "客户端管理",
		icon: "BookOutline",
		path: "/admin/client",
		sequence: 0,
		status: 0,
	},
	{
		id: 6,
		createTime: "2022-09-01 12:15:50",
		createUserId: 2,
		updateTime: "2022-09-01 12:15:50",
		updateUserId: 2,
		parentId: 4,
		type: 3,
		name: "用户管理",
		icon: "BookOutline",
		path: "/user/user",
		sequence: 0,
		status: 0,
	},
	{
		id: 7,
		createTime: "2022-09-01 12:16:24",
		createUserId: 2,
		updateTime: "2022-09-01 12:16:24",
		updateUserId: 2,
		parentId: 4,
		type: 3,
		name: "角色管理",
		icon: "BookOutline",
		path: "/user/role",
		sequence: 0,
		status: 0,
	},
	{
		id: 8,
		createTime: "2022-09-01 12:16:39",
		createUserId: 2,
		updateTime: "2022-09-01 12:16:39",
		updateUserId: 2,
		parentId: 4,
		type: 3,
		name: "权限管理",
		icon: "BookOutline",
		path: "/user/permission",
		sequence: 0,
		status: 0,
	},
	{
		id: 9,
		createTime: "2022-09-01 12:17:06",
		createUserId: 2,
		updateTime: "2022-09-01 12:17:06",
		updateUserId: 2,
		parentId: 4,
		type: 3,
		name: "菜单管理",
		icon: "BookOutline",
		path: "/user/menu",
		sequence: 0,
		status: 0,
	},
	{
		id: 10,
		createTime: "2022-09-01 12:17:06",
		createUserId: 2,
		updateTime: "2022-09-01 12:17:06",
		updateUserId: 2,
		parentId: 6,
		type: 4,
		name: "新增",
		icon: "BookOutline",
		path: "/user/menu",
		sequence: 0,
		status: 0,
	},
];

const errorMockData = {
	id: 99999,
	createTime: "2022-09-01 12:17:06",
	createUserId: 2,
	updateTime: "2022-09-01 12:17:06",
	updateUserId: 2,
	parentId: 6,
	type: 4,
	name: "新增",
	icon: "BookOutline",
	path: "/user/menu",
	sequence: 0,
	status: 0,
};

const createMockService: CreateService = (path: string) => {
	return {
		list(menu) {
			return mockResponse(mockData);
		},
		getById(id) {
			return mockResponse(
				mockData.find((p) => p.id === id) ?? errorMockData
			);
		},
		save(menu) {
			const newId = Math.max(...mockData.map((p) => p.id)) + 1;
			const newMenu = menu as MenuWithId;
			newMenu.id = newId;
			mockData.push(newMenu);
			return mockResponse(newMenu);
		},
		update(menu) {
			const existMenu = mockData.find((p) => p.id === menu.id);
			if (existMenu) {
				Object.keys(menu).forEach((key) => {
					// eslint-disable-next-line @typescript-eslint/ban-ts-comment
					// @ts-ignore
					existMenu[key] = menu[key];
				});
				return mockResponse(existMenu);
			}
			return mockResponse(errorMockData);
		},
		deleteById(id) {
			const index = mockData.findIndex((menu) => menu.id === id);
			if (index != -1) {
				mockData.splice(index, 1);
			}
			return mockResponse("" as never);
		},
		bindAuthorities(bindOneDTO) {
			return mockResponse("" as never);
		},
	};
};

const PATH = "/user/menu";

const menuService = isMockMode ? createMockService(PATH) : createService(PATH);

export { menuService };
export type { Menu, MenuWithId };
