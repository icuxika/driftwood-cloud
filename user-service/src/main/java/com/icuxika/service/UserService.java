package com.icuxika.service;

import com.icuxika.user.entity.User;
import com.icuxika.user.vo.UserVO;

public interface UserService {

    User findByUsername(String username);

    User findByPhone(String phone);

    UserVO getUserInfo();
}
