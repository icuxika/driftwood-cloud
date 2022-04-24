package com.icuxika.modules.user.entity;

import com.icuxika.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "permission_group")
@Entity
public class PermissionGroup extends BaseEntity {

    /**
     * 权限组名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 父权限组id
     */
    @Column(nullable = false)
    private Long parentId;

    /**
     * 描述
     */
    @Column()
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
