package com.icuxika.modules.user.entity;

import com.icuxika.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "permission")
@Entity
public class Permission extends BaseEntity {

    /**
     * 名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 权限
     */
    @Column(nullable = false)
    private String authority;

    /**
     * 权限类型
     * 取值 {@link com.icuxika.dict.PermissionType}
     */
    @Column(nullable = false)
    private Integer type;

    /**
     * 权限所属权限分组id {@link PermissionGroup}
     */
    @Column(nullable = false)
    private Long groupId;

    /**
     * 权限描述
     */
    @Column()
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
