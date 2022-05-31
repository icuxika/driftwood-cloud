package com.icuxika.service;

import com.icuxika.modules.user.dto.BindOneDTO;
import com.icuxika.modules.user.dto.UserDTO;
import com.icuxika.modules.user.entity.User;
import com.icuxika.modules.user.vo.UserAuthVO;
import com.icuxika.modules.user.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserAuthVO findByUsername(String username);

    UserAuthVO findByPhone(String phone);

    UserVO getUserInfo();

    Page<User> getPage(Pageable pageable, User user);

    User getById(Long id);

    void save(UserDTO userDTO);

    void update(UserDTO userDTO);

    void deleteById(Long id);

    void bindRoles(BindOneDTO bindOneDTO);

    void uploadAvatar(MultipartFile file);
}
