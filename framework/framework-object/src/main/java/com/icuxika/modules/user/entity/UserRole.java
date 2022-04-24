package com.icuxika.modules.user.entity;

import com.icuxika.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "user_role")
@Entity
public class UserRole extends BaseEntity {

    /**
     * 用户id
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 角色id
     */
    @Column(nullable = false)
    private Long roleId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}
