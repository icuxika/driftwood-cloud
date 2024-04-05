package com.icuxika.framework.object.modules.user.dto;

import com.icuxika.framework.object.modules.user.entity.Permission;
import com.icuxika.framework.object.modules.user.entity.PermissionGroup;

import java.util.List;

public class AllPermissionDTO {

    private List<PermissionGroup> permissionGroupList;

    private List<Permission> permissionList;

    public List<PermissionGroup> getPermissionGroupList() {
        return permissionGroupList;
    }

    public void setPermissionGroupList(List<PermissionGroup> permissionGroupList) {
        this.permissionGroupList = permissionGroupList;
    }

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }
}
