import { BaseEntity, HasId } from "@/api";

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

export type { Permission, PermissionWithId };
