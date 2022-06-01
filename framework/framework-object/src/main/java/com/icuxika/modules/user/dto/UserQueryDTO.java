package com.icuxika.modules.user.dto;

import com.icuxika.modules.user.entity.User;
import com.icuxika.modules.user.entity.UserProfile;

import java.util.List;

public class UserQueryDTO extends User {

    private UserProfile userProfile;

    /**
     * 出生日期范围
     */
    private List<Long> birthdayRange;

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public List<Long> getBirthdayRange() {
        return birthdayRange;
    }

    public void setBirthdayRange(List<Long> birthdayRange) {
        this.birthdayRange = birthdayRange;
    }
}
