package com.icuxika.framework.object.modules.user.dto;

import com.alibaba.excel.annotation.ExcelProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class UserExcelDTO {

    @ExcelProperty("昵称")
    private String nickname;

    @ExcelProperty("日期")
    private LocalDate localDate;

    @ExcelProperty("时间")
    private LocalTime localTime;

    @ExcelProperty("日期时间")
    private LocalDateTime localDateTime;

    public UserExcelDTO() {
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

    @Override
    public String toString() {
        return "UserExcelDTO{" +
                "nickname='" + nickname + '\'' +
                ", localDate=" + localDate +
                ", localTime=" + localTime +
                ", localDateTime=" + localDateTime +
                '}';
    }
}
