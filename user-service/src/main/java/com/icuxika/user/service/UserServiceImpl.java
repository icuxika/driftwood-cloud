package com.icuxika.user.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.basic.util.BeanExUtil;
import com.icuxika.framework.object.modules.admin.feign.AdminFileClient;
import com.icuxika.framework.object.modules.file.feign.MinioFileClient;
import com.icuxika.framework.object.modules.user.dto.BindOneDTO;
import com.icuxika.framework.object.modules.user.dto.UserDTO;
import com.icuxika.framework.object.modules.user.dto.UserQueryDTO;
import com.icuxika.framework.object.modules.user.entity.*;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
import com.icuxika.framework.object.modules.user.vo.UserExcelVO;
import com.icuxika.framework.object.modules.user.vo.UserInfoVO;
import com.icuxika.framework.object.modules.user.vo.UserVO;
import com.icuxika.framework.security.util.SecurityUtil;
import com.icuxika.user.repository.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLQuery;
import jakarta.persistence.EntityManager;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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

    private final AdminFileClient adminFileClient;

    private final EntityManager entityManager;

    private final CriteriaBuilderFactory criteriaBuilderFactory;

    private final UserOpenAuthRepository userOpenAuthRepository;

    public UserServiceImpl(UserRepository userRepository, UserProfileRepository userProfileRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository, PermissionRepository permissionRepository, MenuPermissionRepository menuPermissionRepository, MenuRepository menuRepository, AdminFileClient adminFileClient, EntityManager entityManager, CriteriaBuilderFactory criteriaBuilderFactory, UserOpenAuthRepository userOpenAuthRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.menuPermissionRepository = menuPermissionRepository;
        this.menuRepository = menuRepository;
        this.adminFileClient = adminFileClient;
        this.entityManager = entityManager;
        this.criteriaBuilderFactory = criteriaBuilderFactory;
        this.userOpenAuthRepository = userOpenAuthRepository;
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
    public UserAuthVO findByOpenid(String openid, Integer type) {
        Optional<UserOpenAuth> userOpenAuthOptional = userOpenAuthRepository.findByOpenidAndType(openid, type);
        if (userOpenAuthOptional.isEmpty()) return null;
        UserOpenAuth userOpenAuth = userOpenAuthOptional.get();
        return userRepository.findById(userOpenAuth.getUserId()).map(this::buildUserAuthVO).orElse(null);
    }

    @Override
    public Boolean findThirdBindByOpenid(String openid, Integer type) {
        return userOpenAuthRepository.findByOpenidAndType(openid, type).isPresent();
    }

    @Override
    public void updateUserIP(Long userId, String ip) {
        userProfileRepository.findByUserId(userId).ifPresent(userProfile -> {
            userProfile.setLastRemoteAddress(userProfile.getRemoteAddress());
            userProfile.setRemoteAddress(ip);
            userProfileRepository.save(userProfile);
        });
    }

    @Override
    public UserInfoVO getUserInfo() {
        User user = userRepository.findById(SecurityUtil.getUserId()).orElseThrow(() -> new GlobalServiceException("用户数据不存在"));
        return buildUserInfoVO(user);
    }

    @Override
    public Page<UserVO> getPage(Pageable pageable, UserQueryDTO userQueryDTO) {
        QUser qUser = QUser.user;
        QUserProfile qUserProfile = QUserProfile.userProfile;

        // 查询条件
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        // user
        if (StringUtils.hasText(userQueryDTO.getUsername())) {
            // 用户名
            booleanBuilder.and(qUser.username.like(userQueryDTO.getUsername()));
        }
        if (StringUtils.hasText(userQueryDTO.getPhone())) {
            // 手机号
            booleanBuilder.and(qUser.phone.startsWith(userQueryDTO.getPhone()));
        }
        if (StringUtils.hasText(userQueryDTO.getNickname())) {
            // 昵称
            booleanBuilder.and(qUser.nickname.like(userQueryDTO.getNickname()));
        }
        if (userQueryDTO.getEnabled() != null) {
            // 账户是否禁用
            booleanBuilder.and(qUser.isEnabled.eq(userQueryDTO.getEnabled()));
        }
        // other
        if (!CollectionUtils.isEmpty(userQueryDTO.getBirthdayRange())) {
            // 出生日期
            List<Long> range = userQueryDTO.getBirthdayRange();
            LocalDate start = LocalDate.ofInstant(Instant.ofEpochMilli(range.get(0)), ZoneId.systemDefault());
            LocalDate end = LocalDate.ofInstant(Instant.ofEpochMilli(range.get(1)), ZoneId.systemDefault());
            booleanBuilder.and(qUserProfile.birthday.between(start, end));
        }

        JPQLQuery<Tuple> jpqlQuery = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                .select(qUser, qUserProfile)
                .from(qUser)
                .leftJoin(qUserProfile)
                .on(qUser.id.eq(qUserProfile.userId))
                .where(booleanBuilder);

        // 获取总数
        long fetchCount = jpqlQuery.fetchCount();
        // 应用分页（会自动应用相关排序）
        Querydsl querydsl = new Querydsl(entityManager, (new PathBuilderFactory()).create(User.class));
        jpqlQuery = querydsl.applyPagination(pageable, jpqlQuery);
        List<Tuple> list = jpqlQuery.fetch();

        List<UserVO> userVOList = list.stream().map(tuple -> {
            User user = tuple.get(qUser);
            UserProfile userProfile = tuple.get(qUserProfile);
            UserVO userVO = new UserVO();
            Optional.ofNullable(user).ifPresent(u -> BeanUtils.copyProperties(u, userVO));
            Optional.ofNullable(userProfile).ifPresent(userVO::setUserProfile);
            return userVO;
        }).collect(Collectors.toList());

        return new PageImpl<>(userVOList, pageable, fetchCount);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(UserDTO userDTO) {
        userRepository.findByUsername(userDTO.getUsername()).ifPresent(exist -> {
            throw new GlobalServiceException("用户名已被使用");
        });
        userRepository.findByPhone(userDTO.getPhone()).ifPresent(exist -> {
            throw new GlobalServiceException("手机号已被使用");
        });
        User user = new User();
        BeanUtils.copyProperties(userDTO, user, BeanExUtil.getIgnorePropertyArray(userDTO));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        userRepository.save(user);

        UserProfile userProfile = new UserProfile();
        BeanUtils.copyProperties(userDTO, userProfile, BeanExUtil.getIgnorePropertyArray(userDTO));
        userProfile.setUserId(user.getId());
        userProfileRepository.save(userProfile);
    }

    @Override
    public void update(UserDTO userDTO) {
        User exist = userRepository.findById(userDTO.getId()).orElseThrow(() -> new GlobalServiceException("数据不存在"));
        BeanUtils.copyProperties(userDTO, exist, BeanExUtil.getIgnorePropertyArray(userDTO));
        if (StringUtils.hasText(userDTO.getPassword())) {
            exist.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userRepository.save(exist);

        userProfileRepository.findByUserId(userDTO.getId()).ifPresent(userProfile -> {
            BeanUtils.copyProperties(userDTO, userProfile, BeanExUtil.getIgnorePropertyArray(userDTO));
            userProfileRepository.save(userProfile);
        });
    }

    @Override
    public void deleteById(Long id) {
        userRepository.logicDeleteById(id);
    }

    @Override
    public void bindRoles(BindOneDTO bindOneDTO) {
        if (!userRepository.existsById(bindOneDTO.getId())) {
            throw new GlobalServiceException("用户数据不存在");
        }
        if (roleRepository.findByIdIn(bindOneDTO.getIdList()).size() != bindOneDTO.getIdList().size()) {
            throw new GlobalServiceException("角色数据不存在");
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
        ApiData<Long> minioFileVOApiData = adminFileClient.uploadFile(file);
        if (!minioFileVOApiData.isSuccess()) {
            throw new GlobalServiceException("头像上传失败");
        }
        userProfileRepository.findByUserId(SecurityUtil.getUserId()).ifPresent(userProfile -> {
            userProfile.setAvatarFileId(minioFileVOApiData.getData());
            userProfileRepository.save(userProfile);
        });
    }

    @Override
    public List<UserExcelVO> export(UserQueryDTO userQueryDTO) {
        JPQLQuery<Tuple> jpqlQuery = buildQuery(userQueryDTO);
        List<Tuple> list = jpqlQuery.fetch();
        return list.stream().map(tuple -> {
            User user = tuple.get(QUser.user);
            UserExcelVO userExcelVO = new UserExcelVO();
            userExcelVO.setUsername(user.getUsername());
            userExcelVO.setPhone(user.getPhone());
            userExcelVO.setNickname(user.getNickname());
            return userExcelVO;
        }).collect(Collectors.toList());
    }

    /**
     * 查询用户详细信息（不包含密码等）
     *
     * @param user 用户
     * @return 用户详细信息
     */
    private UserInfoVO buildUserInfoVO(User user) {
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setNickname(user.getNickname());
        setUserAuthInfo(user.getId(), userInfoVO::setRoleList, userInfoVO::setPermissionList, userInfoVO::setMenuList);
        userProfileRepository.findByUserId(user.getId()).ifPresent(userInfoVO::setUserProfile);
        return userInfoVO;
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

    private JPQLQuery<Tuple> buildQuery(UserQueryDTO userQueryDTO) {
        QUser qUser = QUser.user;
        QUserProfile qUserProfile = QUserProfile.userProfile;

        // 查询条件
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        // user
        if (StringUtils.hasText(userQueryDTO.getUsername())) {
            // 用户名
            booleanBuilder.and(qUser.username.like(userQueryDTO.getUsername()));
        }
        if (StringUtils.hasText(userQueryDTO.getPhone())) {
            // 手机号
            booleanBuilder.and(qUser.phone.startsWith(userQueryDTO.getPhone()));
        }
        if (StringUtils.hasText(userQueryDTO.getNickname())) {
            // 昵称
            booleanBuilder.and(qUser.nickname.like(userQueryDTO.getNickname()));
        }
        if (userQueryDTO.getEnabled() != null) {
            // 账户是否禁用
            booleanBuilder.and(qUser.isEnabled.eq(userQueryDTO.getEnabled()));
        }
        // other
        if (!CollectionUtils.isEmpty(userQueryDTO.getBirthdayRange())) {
            // 出生日期
            List<Long> range = userQueryDTO.getBirthdayRange();
            LocalDate start = LocalDate.ofInstant(Instant.ofEpochMilli(range.get(0)), ZoneId.systemDefault());
            LocalDate end = LocalDate.ofInstant(Instant.ofEpochMilli(range.get(1)), ZoneId.systemDefault());
            booleanBuilder.and(qUserProfile.birthday.between(start, end));
        }

        JPQLQuery<Tuple> jpqlQuery = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                .select(qUser, qUserProfile)
                .from(qUser)
                .leftJoin(qUserProfile)
                .on(qUser.id.eq(qUserProfile.userId))
                .where(booleanBuilder);

        return jpqlQuery;
    }
}
