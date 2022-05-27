package com.icuxika.service;

import com.icuxika.common.ApiData;
import com.icuxika.exception.GlobalServiceException;
import com.icuxika.modules.file.feign.FileClient;
import com.icuxika.modules.file.vo.MinioFileVO;
import com.icuxika.modules.user.dto.BindOneDTO;
import com.icuxika.modules.user.entity.*;
import com.icuxika.modules.user.vo.UserAuthVO;
import com.icuxika.modules.user.vo.UserVO;
import com.icuxika.repository.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final UserRepository userRepository;

    private final UserProfileRepository userProfileRepository;

    private final UserRoleRepository userRoleRepository;

    private final RoleRepository roleRepository;

    private final RolePermissionRepository rolePermissionRepository;

    private final PermissionRepository permissionRepository;

    private final MenuPermissionRepository menuPermissionRepository;

    private final MenuRepository menuRepository;

    private final FileClient fileClient;

    private static final String USER_AVATAR_FILE_PATH_PREFIX = "user/avatar";

    public UserServiceImpl(UserRepository userRepository, UserProfileRepository userProfileRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository, PermissionRepository permissionRepository, MenuPermissionRepository menuPermissionRepository, MenuRepository menuRepository, FileClient fileClient) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.menuPermissionRepository = menuPermissionRepository;
        this.menuRepository = menuRepository;
        this.fileClient = fileClient;
    }

    @Override
    public UserAuthVO findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::buildUserAuthVO).orElse(null);
    }

    @Override
    public UserAuthVO findByPhone(String phone) {
        return userRepository.findByPhone(phone).map(this::buildUserAuthVO).orElse(null);
    }

    @Override
    public UserVO getUserInfo() {
        Long currentUserId = SecurityUtil.getUserId();
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new RuntimeException("用户数据不存在"));
        return buildUserVO(user);
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

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void update(User user) {
        User exist = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("数据不存在"));
        BeanUtils.copyProperties(user, exist, BeanExUtil.getIgnorePropertyArray(user));
        if (StringUtils.hasText(user.getPassword())) {
            exist.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(exist);
    }

    @Override
    public void deleteById(Long id) {
//        if (userRepository.existsById(id)) {
//            userRepository.deleteById(id);
//        }
        userRepository.logicDeleteById(id);
    }

    @Override
    public void bindRoles(BindOneDTO bindOneDTO) {
        if (!userRepository.existsById(bindOneDTO.getId())) {
            throw new RuntimeException("用户数据不存在");
        }
        if (roleRepository.findByIdIn(bindOneDTO.getIdList()).size() != bindOneDTO.getIdList().size()) {
            throw new RuntimeException("角色数据不存在");
        }
        List<UserRole> existList = userRoleRepository.findByRoleIdIn(bindOneDTO.getIdList());
        List<UserRole> userRoleList = bindOneDTO.getIdList().stream().map(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(bindOneDTO.getId());
            userRole.setRoleId(roleId);
            existList.stream().filter(exist -> exist.getUserId().equals(bindOneDTO.getId()) && exist.getRoleId().equals(roleId)).findFirst().ifPresent(exist -> userRole.setId(exist.getId()));
            return userRole;
        }).collect(Collectors.toList());
        userRoleRepository.saveAll(userRoleList);
    }

    @Override
    public void uploadAvatar(MultipartFile file) {
        ApiData<MinioFileVO> minioFileVOApiData = fileClient.uploadFile(file, USER_AVATAR_FILE_PATH_PREFIX);
        if (!minioFileVOApiData.isSuccess()) {
            throw new GlobalServiceException("头像上传失败");
        }
        MinioFileVO minioFileVO = minioFileVOApiData.getData();
        long currentUserId = SecurityUtil.getUserId();
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserId(currentUserId);
        if (userProfileOptional.isPresent()) {
            UserProfile userProfile = userProfileOptional.get();
            userProfile.setAvatar(minioFileVO.getFullPath());
            userProfileRepository.save(userProfile);
        } else {
            // TODO 用户资料应在用户新建时进行初始化
            UserProfile newUserProfile = new UserProfile();
            newUserProfile.setUserId(currentUserId);
            newUserProfile.setAvatar(minioFileVO.getFullPath());
            userProfileRepository.save(newUserProfile);
        }
    }

    /**
     * 查询用户详细信息（不包含密码等）
     *
     * @param user 用户
     * @return 用户详细信息
     */
    private UserVO buildUserVO(User user) {
        UserVO userVO = new UserVO();
        userVO.setUserId(user.getId());
        userVO.setNickname(user.getNickname());
        setUserAuthInfo(user.getId(), userVO::setRoleList, userVO::setPermissionList, userVO::setMenuList);
        userProfileRepository.findByUserId(user.getId()).ifPresent(userVO::setUserProfile);
        return userVO;
    }

    /**
     * 查询用户详细信息
     *
     * @param user 用户
     * @return 用户详细信息
     */
    private UserAuthVO buildUserAuthVO(User user) {
        UserAuthVO userAuthVO = new UserAuthVO();
        BeanUtils.copyProperties(user, userAuthVO);
        setUserAuthInfo(user.getId(), userAuthVO::setRoleList, userAuthVO::setPermissionList, menus -> {
        });
        return userAuthVO;
    }

    /**
     * 填充用户角色权限等信息
     *
     * @param userId           用户ID
     * @param roleSetter       角色填充器
     * @param permissionSetter 权限填充器
     * @param menuSetter       菜单填充器
     */
    private void setUserAuthInfo(Long userId, Consumer<List<Role>> roleSetter, Consumer<List<Permission>> permissionSetter, Consumer<List<Menu>> menuSetter) {
        // 角色
        List<Long> roleIdList = userRoleRepository.findByUserId(userId).stream().map(UserRole::getRoleId).collect(Collectors.toList());
        List<Role> roleList = roleIdList.isEmpty() ? Collections.emptyList() : roleRepository.findByIdIn(roleIdList);
        roleSetter.accept(roleList);

        // 权限
        List<Long> permissionIdList = roleIdList.isEmpty() ? Collections.emptyList() : rolePermissionRepository.findByRoleIdIn(roleIdList).stream().map(RolePermission::getPermissionId).distinct().collect(Collectors.toList());
        List<Permission> permissionList = permissionIdList.isEmpty() ? Collections.emptyList() : permissionRepository.findByIdIn(permissionIdList);
        permissionSetter.accept(permissionList);

        // 菜单
        List<Long> menuIdList = permissionIdList.isEmpty() ? Collections.emptyList() : menuPermissionRepository.findByPermissionIdIn(permissionIdList).stream().map(MenuPermission::getMenuId).distinct().collect(Collectors.toList());
        List<Menu> menuList = menuIdList.isEmpty() ? Collections.emptyList() : menuRepository.findByIdIn(menuIdList);
        menuSetter.accept(menuList);
    }
}
