package com.icuxika.user.entity;

import com.icuxika.common.BaseEntity;
import com.icuxika.dict.SystemStatusType;

import javax.persistence.*;

@Table(name = "role")
@Entity
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    @Column(nullable = false)
    private String name;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
