package com.icuxika.service;

import com.icuxika.user.dto.BindOneDTO;
import com.icuxika.user.entity.User;
import com.icuxika.user.vo.UserAuthVO;
import com.icuxika.user.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserAuthVO findByUsername(String username);

    UserAuthVO findByPhone(String phone);

    UserVO getUserInfo();

    Page<User> getPage(Pageable pageable, User user);

    User getById(Long id);

    void save(User user);

    void update(User user);

    void deleteById(Long id);

    void bindRoles(BindOneDTO bindOneDTO);
}
