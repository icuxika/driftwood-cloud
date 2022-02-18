package com.icuxika.service;

import com.icuxika.repository.*;
import com.icuxika.user.entity.User;
import com.icuxika.user.vo.UserVO;
import com.icuxika.util.SecurityUtil;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final RoleRepository roleRepository;

    private final RolePermissionRepository rolePermissionRepository;

    private final PermissionRepository permissionRepository;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public UserVO getUserInfo() {
        Long userId = SecurityUtil.getUserId();
        User user = userRepository.getById(userId);
        UserVO userVO = new UserVO();
        userVO.setUserId(userId);
        userVO.setNickname(user.getNickname());
        return userVO;
    }

}
