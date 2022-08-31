package com.icuxika.framework.object.modules.user.entity;

import com.icuxika.framework.basic.dict.MenuType;
import com.icuxika.framework.basic.dict.SystemStatusType;
import com.icuxika.framework.object.base.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "menu")
@Entity
public class Menu extends BaseEntity {

    /**
     * 父菜单id
     */
    @Column(nullable = false)
    private Long parentId;

    /**
     * 菜单类型
     * 取值 {@link MenuType}
     */
    @Column(nullable = false)
    private Integer type;

    /**
     * 菜单名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 菜单图标
     */
    @Column()
    private String icon;

    /**
     * 菜单路径（组件）
     */
    @Column(nullable = false)
    private String path;

    /**
     * 菜单排序
     */
    @Column(nullable = false)
    private Integer sequence;

    /**
     * 菜单状态
     * 取值 {@link SystemStatusType}
     */
    private Integer status;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
