package com.icuxika.user.vo;

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
}
