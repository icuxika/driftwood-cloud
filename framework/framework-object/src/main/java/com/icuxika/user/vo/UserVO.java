package com.icuxika.user.vo;

import com.icuxika.user.entity.Menu;
import com.icuxika.user.entity.Permission;
import com.icuxika.user.entity.Role;

import java.util.List;

public class UserVO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户具有的角色集合
     */
    private List<Role> roleList;

    /**
     * 用户具有的权限集合
     */
    private List<Permission> permissionList;

    /**
     * 用户能够访问的菜单集合
     */
    private List<Menu> menuList;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

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

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }
}
