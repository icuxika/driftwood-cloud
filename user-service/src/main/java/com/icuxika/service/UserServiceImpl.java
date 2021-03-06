package com.icuxika.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.icuxika.common.ApiData;
import com.icuxika.exception.GlobalServiceException;
import com.icuxika.modules.file.feign.FileClient;
import com.icuxika.modules.file.vo.MinioFileVO;
import com.icuxika.modules.user.dto.BindOneDTO;
import com.icuxika.modules.user.dto.UserDTO;
import com.icuxika.modules.user.dto.UserQueryDTO;
import com.icuxika.modules.user.entity.*;
import com.icuxika.modules.user.vo.UserAuthVO;
import com.icuxika.modules.user.vo.UserInfoVO;
import com.icuxika.modules.user.vo.UserVO;
import com.icuxika.repository.*;
import com.icuxika.util.BeanExUtil;
import com.icuxika.util.SecurityUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLQuery;
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

import javax.persistence.EntityManager;
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

    private final FileClient fileClient;

    private final EntityManager entityManager;

    private final CriteriaBuilderFactory criteriaBuilderFactory;

    private static final String USER_AVATAR_FILE_PATH_PREFIX = "user/avatar";

    public UserServiceImpl(UserRepository userRepository, UserProfileRepository userProfileRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository, PermissionRepository permissionRepository, MenuPermissionRepository menuPermissionRepository, MenuRepository menuRepository, FileClient fileClient, EntityManager entityManager, CriteriaBuilderFactory criteriaBuilderFactory) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.menuPermissionRepository = menuPermissionRepository;
        this.menuRepository = menuRepository;
        this.fileClient = fileClient;
        this.entityManager = entityManager;
        this.criteriaBuilderFactory = criteriaBuilderFactory;
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
    public void updateUserIP(Long userId, String ip) {
        userProfileRepository.findByUserId(userId).ifPresent(userProfile -> {
            userProfile.setLastRemoteAddress(userProfile.getRemoteAddress());
            userProfile.setRemoteAddress(ip);
            userProfileRepository.save(userProfile);
        });
    }

    @Override
    public UserInfoVO getUserInfo() {
        User user = userRepository.findById(SecurityUtil.getUserId()).orElseThrow(() -> new GlobalServiceException("?????????????????????"));
        return buildUserInfoVO(user);
    }

    @Override
    public Page<UserVO> getPage(Pageable pageable, UserQueryDTO userQueryDTO) {
        QUser qUser = QUser.user;
        QUserProfile qUserProfile = QUserProfile.userProfile;

        // ????????????
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        // user
        if (StringUtils.hasText(userQueryDTO.getUsername())) {
            // ?????????
            booleanBuilder.and(qUser.username.like(userQueryDTO.getUsername()));
        }
        if (StringUtils.hasText(userQueryDTO.getPhone())) {
            // ?????????
            booleanBuilder.and(qUser.phone.startsWith(userQueryDTO.getPhone()));
        }
        if (StringUtils.hasText(userQueryDTO.getNickname())) {
            // ??????
            booleanBuilder.and(qUser.nickname.like(userQueryDTO.getNickname()));
        }
        if (userQueryDTO.getEnabled() != null) {
            // ??????????????????
            booleanBuilder.and(qUser.isEnabled.eq(userQueryDTO.getEnabled()));
        }
        // other
        if (!CollectionUtils.isEmpty(userQueryDTO.getBirthdayRange())) {
            // ????????????
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

        // ????????????
        long fetchCount = jpqlQuery.fetchCount();
        // ?????????????????????????????????????????????
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
            throw new GlobalServiceException("?????????????????????");
        });
        userRepository.findByPhone(userDTO.getPhone()).ifPresent(exist -> {
            throw new GlobalServiceException("?????????????????????");
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
        User exist = userRepository.findById(userDTO.getId()).orElseThrow(() -> new GlobalServiceException("???????????????"));
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
            throw new GlobalServiceException("?????????????????????");
        }
        if (roleRepository.findByIdIn(bindOneDTO.getIdList()).size() != bindOneDTO.getIdList().size()) {
            throw new GlobalServiceException("?????????????????????");
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
            throw new GlobalServiceException("??????????????????");
        }
        MinioFileVO minioFileVO = minioFileVOApiData.getData();
        userProfileRepository.findByUserId(SecurityUtil.getUserId()).ifPresent(userProfile -> {
            userProfile.setAvatar(minioFileVO.getFullPath());
            userProfileRepository.save(userProfile);
        });
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param user ??????
     * @return ??????????????????
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
     * ????????????????????????
     *
     * @param user ??????
     * @return ??????????????????
     */
    private UserAuthVO buildUserAuthVO(User user) {
        UserAuthVO userAuthVO = new UserAuthVO();
        BeanUtils.copyProperties(user, userAuthVO);
        setUserAuthInfo(user.getId(), userAuthVO::setRoleList, userAuthVO::setPermissionList, menus -> {
        });
        return userAuthVO;
    }

    /**
     * ?????????????????????????????????
     *
     * @param userId           ??????ID
     * @param roleSetter       ???????????????
     * @param permissionSetter ???????????????
     * @param menuSetter       ???????????????
     */
    private void setUserAuthInfo(Long userId, Consumer<List<Role>> roleSetter, Consumer<List<Permission>> permissionSetter, Consumer<List<Menu>> menuSetter) {
        // ??????
        List<Long> roleIdList = userRoleRepository.findByUserId(userId).stream().map(UserRole::getRoleId).collect(Collectors.toList());
        List<Role> roleList = roleIdList.isEmpty() ? Collections.emptyList() : roleRepository.findByIdIn(roleIdList);
        roleSetter.accept(roleList);

        // ??????
        List<Long> permissionIdList = roleIdList.isEmpty() ? Collections.emptyList() : rolePermissionRepository.findByRoleIdIn(roleIdList).stream().map(RolePermission::getPermissionId).distinct().collect(Collectors.toList());
        List<Permission> permissionList = permissionIdList.isEmpty() ? Collections.emptyList() : permissionRepository.findByIdIn(permissionIdList);
        permissionSetter.accept(permissionList);

        // ??????
        List<Long> menuIdList = permissionIdList.isEmpty() ? Collections.emptyList() : menuPermissionRepository.findByPermissionIdIn(permissionIdList).stream().map(MenuPermission::getMenuId).distinct().collect(Collectors.toList());
        List<Menu> menuList = menuIdList.isEmpty() ? Collections.emptyList() : menuRepository.findByIdIn(menuIdList);
        menuSetter.accept(menuList);
    }
}
