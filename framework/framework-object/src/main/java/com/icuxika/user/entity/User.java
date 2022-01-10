package com.icuxika.user.entity;

import com.icuxika.common.BaseEntity;

import javax.persistence.*;

@Table(name = "user")
@Entity
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * 密码
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 手机号
     */
    @Column(name = "phone", nullable = false)
    private String phone;

    /**
     * 昵称
     */
    @Column(name = "nickname", nullable = false)
    private String nickname;

    /**
     * 账户是否没有过期
     */
    @Column(name = "is_account_non_expired", nullable = false)
    private Boolean isAccountNonExpired;

    /**
     * 账户是否没有冻结
     */
    @Column(name = "is_account_non_locked", nullable = false)
    private Boolean isAccountNonLocked;

    /**
     * 账户凭证是否没有过期
     */
    @Column(name = "is_credential_is_non_expired", nullable = false)
    private Boolean isCredentialsNonExpired;

    /**
     * 账户是否启用
     */
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getAccountNonExpired() {
        return isAccountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public Boolean getAccountNonLocked() {
        return isAccountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public Boolean getCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }
}
