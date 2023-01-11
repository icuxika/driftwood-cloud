package com.icuxika.framework.object.modules.user.entity;

import com.icuxika.framework.object.base.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "menu_permission")
@Entity
public class MenuPermission extends BaseEntity {

    /**
     * 菜单id
     */
    @Column(nullable = false)
    private Long menuId;

    /**
     * 权限id
     */
    @Column(nullable = false)
    private Long permissionId;

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }
}
