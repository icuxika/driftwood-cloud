package com.icuxika.framework.object.modules.user.entity;

import com.icuxika.framework.object.base.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "user_open_auth")
@Entity
public class UserOpenAuth extends BaseEntity {

    /**
     * 用户id
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 用户唯一标识的 openid
     */
    @Column(name = "openid", nullable = false)
    private String openid;

    /**
     * 第三方登录类型
     * 取值 {@link com.icuxika.framework.basic.dict.OpenAuthType}
     */
    @Column(nullable = false)
    private Integer type;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
