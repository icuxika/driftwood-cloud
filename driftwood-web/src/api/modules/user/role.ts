import { BaseEntity } from "@/api";

/**
 * 角色
 */
interface Role extends BaseEntity {
	name: string;
	role: string;
}

export type { Role };
