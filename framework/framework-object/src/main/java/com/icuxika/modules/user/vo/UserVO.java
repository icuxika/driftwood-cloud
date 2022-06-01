package com.icuxika.modules.user.vo;

import com.icuxika.modules.user.entity.User;
import com.icuxika.modules.user.entity.UserProfile;

public class UserVO extends User {

    private UserProfile userProfile;

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
