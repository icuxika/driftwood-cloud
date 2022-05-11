import { BaseEntity } from "@/api";

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

export { Permission };
