package com.icuxika.modules.user.entity;

import com.icuxika.common.BaseEntity;
import com.icuxika.common.validation.Gender;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Table(name = "user_profile")
@Entity
public class UserProfile extends BaseEntity {

    /**
     * 用户id
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 头像地址（头像属于不敏感的文件数据，不以文件id方式存储）
     */
    @Column(name = "avatar", nullable = true)
    private String avatar;

    /**
     * 国家
     */
    @Column(name = "nation", nullable = true)
    private String nation;

    /**
     * 省
     */
    @Column(name = "province", nullable = true)
    private String province;

    /**
     * 市
     */
    @Column(name = "city", nullable = true)
    private String city;

    /**
     * 区
     */
    @Column(name = "district", nullable = true)
    private String district;

    /**
     * 街道
     */
    @Column(name = "street", nullable = true)
    private String street;

    /**
     * 门牌
     */
    @Column(name = "street_number", nullable = true)
    private String streetNumber;

    /**
     * 上一次登录的ip地址
     */
    @Column(name = "last_remote_address", nullable = true)
    private String lastRemoteAddress;

    /**
     * 本次登录的ip地址
     */
    @Column(name = "remote_address", nullable = true)
    private String remoteAddress;

    /**
     * 出生日期
     */
    @Column(name = "birthday", nullable = true)
    private LocalDate birthday;

    /**
     * 性别
     */
    @Gender
    @Column(name = "gender", nullable = true)
    private Integer gender;

    /**
     * 个性签名
     */
    @Column(name = "signature", nullable = true)
    private String signature;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getLastRemoteAddress() {
        return lastRemoteAddress;
    }

    public void setLastRemoteAddress(String lastRemoteAddress) {
        this.lastRemoteAddress = lastRemoteAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
