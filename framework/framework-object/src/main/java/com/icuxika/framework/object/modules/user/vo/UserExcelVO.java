package com.icuxika.framework.object.modules.user.vo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

@ExcelIgnoreUnannotated
public class UserExcelVO {
    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("昵称")
    private String nickname;

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
}
