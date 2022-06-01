package com.icuxika.modules.user.dto;

import com.icuxika.modules.user.entity.User;

import java.util.List;

public class UserQueryDTO extends User {

    /**
     * 出生日期范围
     */
    private List<Long> birthdayRange;

    public List<Long> getBirthdayRange() {
        return birthdayRange;
    }

    public void setBirthdayRange(List<Long> birthdayRange) {
        this.birthdayRange = birthdayRange;
    }
}
