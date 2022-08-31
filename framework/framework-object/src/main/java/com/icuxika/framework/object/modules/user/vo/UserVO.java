package com.icuxika.framework.object.modules.user.vo;

import com.icuxika.framework.object.modules.user.entity.User;
import com.icuxika.framework.object.modules.user.entity.UserProfile;

public class UserVO extends User {

    private UserProfile userProfile;

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
