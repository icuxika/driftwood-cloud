package com.icuxika.framework.object.modules.user.entity;

import com.icuxika.framework.object.base.common.DeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "role")
@Entity
public class Role extends DeletableEntity {

    /**
     * 角色名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 角色
     */
    @Column(nullable = false)
    private String role;

    /**
     * 角色描述
     */
    @Column()
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
