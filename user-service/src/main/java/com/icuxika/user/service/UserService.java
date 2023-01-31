package com.icuxika.user.service;

import com.icuxika.framework.object.modules.user.dto.BindOneDTO;
import com.icuxika.framework.object.modules.user.dto.UserDTO;
import com.icuxika.framework.object.modules.user.dto.UserQueryDTO;
import com.icuxika.framework.object.modules.user.entity.User;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
import com.icuxika.framework.object.modules.user.vo.UserExcelVO;
import com.icuxika.framework.object.modules.user.vo.UserInfoVO;
import com.icuxika.framework.object.modules.user.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserAuthVO findByUsername(String username);

    UserAuthVO findByPhone(String phone);

    UserAuthVO findByOpenid(String openid, Integer type);

    Boolean findThirdBindByOpenid(String openid, Integer type);

    void updateUserIP(Long userId, String ip);

    UserInfoVO getUserInfo();

    Page<UserVO> getPage(Pageable pageable, UserQueryDTO userQueryDTO);

    User getById(Long id);

    void save(UserDTO userDTO);

    void update(UserDTO userDTO);

    void deleteById(Long id);

    void bindRoles(BindOneDTO bindOneDTO);

    void uploadAvatar(MultipartFile file);

    List<UserExcelVO> export(UserQueryDTO userQueryDTO);
}
