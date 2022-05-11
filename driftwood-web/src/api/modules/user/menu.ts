import { BaseEntity } from "@/api";

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

export { Menu };
