import { Page, Pageable, resolveAxiosResult } from "@/api";
import { Role, roleService } from "@/api/modules/user/role";
import { defineStore } from "pinia";

interface RoleState {}

export const useRoleStore = defineStore("role", {
    state: (): RoleState => ({}),
    getters: {},
    actions: {
        async page(
            pageable: Partial<Pageable & Role>
        ): Promise<Page<Role> | null> {
            return resolveAxiosResult(() => roleService.page(pageable));
        },
    },
});
