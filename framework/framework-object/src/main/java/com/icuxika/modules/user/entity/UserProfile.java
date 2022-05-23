package com.icuxika.modules.user.entity;

import com.icuxika.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Table(name = "user_profile")
@Entity
public class UserProfile extends BaseEntity {

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
    @Column(name = "gender", nullable = true)
    private Integer gender;

    /**
     * 个性签名
     */
    @Column(name = "signature", nullable = true)
    private String signature;
}
