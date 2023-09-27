package com.icuxika.framework.object.modules.user.vo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@ExcelIgnoreUnannotated
public class UserExcelVO {
    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("昵称")
    private String nickname;

    @ExcelProperty("日期")
    private LocalDate localDate;

    @ExcelProperty("时间")
    private LocalTime localTime;

    @ExcelProperty("日期时间")
    private LocalDateTime localDateTime;

    public UserExcelVO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}