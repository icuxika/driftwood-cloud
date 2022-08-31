package com.icuxika.framework.object.modules.user.entity;

import com.icuxika.framework.object.base.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "role_permission")
@Entity
public class RolePermission extends BaseEntity {

    /**
     * 角色id
     */
    @Column(nullable = false)
    private Long roleId;

    /**
     * 权限id
     */
    @Column(nullable = false)
    private Long permissionId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }
}
