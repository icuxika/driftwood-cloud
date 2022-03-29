package com.icuxika.service;

import com.icuxika.user.entity.User;
import com.icuxika.user.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User findByUsername(String username);

    User findByPhone(String phone);

    UserVO getUserInfo();

    Page<User> getPage(Pageable pageable, User user);

    User getById(Long id);

    void save(User user);

    void update(User user);

    void deleteById(Long id);
}
