package com.icuxika.modules.user.dto;

import com.icuxika.common.validation.Gender;
import com.icuxika.common.validation.Phone;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

public class UserDTO {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Length(min = 4, message = "用户名长度应大于4位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 4, message = "密码长度应大于4位")
    private String password;

    @NotBlank(message = "手机号不能为空")
    @Phone
    private String phone;

    @NotBlank(message = "昵称不能为空")
    private String nickname;
    private String nation;
    private String province;
    private String city;
    private String district;
    private String street;
    private String streetNumber;
    private LocalDate birthday;

    @Gender(message = "性别不能为空")
    private Integer gender;
    private String signature;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
