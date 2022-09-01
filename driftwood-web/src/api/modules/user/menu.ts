import { BaseEntity, HasId } from "@/api";

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

export type { Menu, MenuWithId };
