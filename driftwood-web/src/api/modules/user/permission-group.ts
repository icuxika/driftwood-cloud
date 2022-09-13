import { BaseEntity, HasId } from "@/api";

interface PermissionGroup extends BaseEntity {
	name: string;
	parentId: number;
	description: string;
}

type PermissionGroupWithId = PermissionGroup & HasId;

export type { PermissionGroup, PermissionGroupWithId };
