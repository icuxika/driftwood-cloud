package com.icuxika.user.entity;

import com.icuxika.common.BaseEntity;
import com.icuxika.dict.MenuType;
import com.icuxika.dict.SystemStatusType;

import javax.persistence.*;

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
    private Integer sort;

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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
