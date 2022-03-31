package com.icuxika.user.vo;

import com.icuxika.user.entity.Permission;
import com.icuxika.user.entity.Role;
import com.icuxika.user.entity.User;

import java.util.List;

public class UserAuthVO extends User {

    /**
     * 用户具有的角色集合
     */
    private List<Role> roleList;

    /**
     * 用户具有的权限集合
     */
    private List<Permission> permissionList;

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }
}
