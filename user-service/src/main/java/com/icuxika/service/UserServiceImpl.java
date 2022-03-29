package com.icuxika.service;

import com.icuxika.repository.*;
import com.icuxika.user.entity.*;
import com.icuxika.user.vo.UserVO;
import com.icuxika.util.BeanExUtil;
import com.icuxika.util.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final RoleRepository roleRepository;

    private final RolePermissionRepository rolePermissionRepository;

    private final PermissionRepository permissionRepository;

    private final MenuPermissionRepository menuPermissionRepository;

    private final MenuRepository menuRepository;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository, PermissionRepository permissionRepository, MenuPermissionRepository menuPermissionRepository, MenuRepository menuRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.menuPermissionRepository = menuPermissionRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone).orElse(null);
    }

    @Override
    public UserVO getUserInfo() {
        Long currentUserId = SecurityUtil.getUserId();
        if (!userRepository.existsById(currentUserId)) {
            throw new RuntimeException("用户数据不存在");
        }
        User user = userRepository.getById(currentUserId);

        // 角色
        List<Long> roleIdList = userRoleRepository.findByUserIdEquals(currentUserId).stream().map(UserRole::getRoleId).collect(Collectors.toList());
        List<Role> roleList = roleIdList.isEmpty() ? Collections.emptyList() : roleRepository.findByIdIn(roleIdList);

        // 权限
        List<Long> permissionIdList = roleIdList.isEmpty() ? Collections.emptyList() : rolePermissionRepository.findByRoleIdIn(roleIdList).stream().map(RolePermission::getPermissionId).distinct().collect(Collectors.toList());
        List<Permission> permissionList = permissionIdList.isEmpty() ? Collections.emptyList() : permissionRepository.findByIdIn(permissionIdList);

        // 菜单
        List<Long> menuIdList = permissionIdList.isEmpty() ? Collections.emptyList() : menuPermissionRepository.findByPermissionIdIn(permissionIdList).stream().map(MenuPermission::getMenuId).distinct().collect(Collectors.toList());
        List<Menu> menuList = menuIdList.isEmpty() ? Collections.emptyList() : menuRepository.findByIdIn(menuIdList);

        UserVO userVO = new UserVO();
        userVO.setUserId(currentUserId);
        userVO.setNickname(user.getNickname());
        userVO.setRoleList(roleList);
        userVO.setPermissionList(permissionList);
        userVO.setMenuList(menuList);
        return userVO;
    }

    @Override
    public Page<User> getPage(Pageable pageable, User user) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("nickname", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<User> example = Example.of(user, matcher);
        return userRepository.findAll(example, pageable);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void save(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(exist -> {
            throw new RuntimeException("用户名已被使用");
        });
        userRepository.findByPhone(user.getPhone()).ifPresent(exist -> {
            throw new RuntimeException("手机号已被使用");
        });

        Long currentUserId = SecurityUtil.getUserId();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCreateTime(LocalDateTime.now());
        user.setCreateUserId(currentUserId);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateUserId(currentUserId);
        userRepository.save(user);
    }

    @Override
    public void update(User user) {
        User exist = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("数据不存在"));
        BeanUtils.copyProperties(user, exist, BeanExUtil.getIgnorePropertyArray(user));
        if (StringUtils.hasText(user.getPassword())) {
            exist.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        exist.setUpdateTime(LocalDateTime.now());
        exist.setUpdateUserId(SecurityUtil.getUserId());
        userRepository.save(exist);
    }

    @Override
    public void deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }

}
