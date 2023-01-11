package com.icuxika.user.repository;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.object.modules.user.entity.QUser;
import com.icuxika.framework.object.modules.user.entity.QUserProfile;
import com.icuxika.framework.object.modules.user.entity.User;
import com.icuxika.framework.object.modules.user.entity.UserProfile;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest()
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CriteriaBuilderFactory criteriaBuilderFactory;

    @Test
    void save() {
        PasswordEncoder passwordEncoder =
                PasswordEncoderFactories.createDelegatingPasswordEncoder();

        User user = new User();
        user.setUsername("icuxika");
        user.setPassword(passwordEncoder.encode("rbj549232512"));
        user.setPhone("18752065699");
        user.setNickname("浮木");
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCreateTime(LocalDateTime.now());
        user.setCreateUserId(SystemConstant.SYSTEM_CREATE_USER_ID);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateUserId(SystemConstant.SYSTEM_CREATE_USER_ID);
        userRepository.save(user);
    }

    @Test
    void pageByJoin() {
        QUser qUser = QUser.user;
        QUserProfile qUserProfile = QUserProfile.userProfile;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qUser.username.eq("icuxika"));
        JPQLQuery<Tuple> jpqlQuery = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                .select(qUser, qUserProfile)
                .from(qUser)
                .leftJoin(qUserProfile)
                .on(qUser.id.eq(qUserProfile.userId))
                .where(booleanBuilder);
        long fetchCount = jpqlQuery.fetchCount();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());
        Querydsl querydsl = new Querydsl(entityManager, new PathBuilderFactory().create(User.class));
        jpqlQuery = querydsl.applyPagination(pageable, jpqlQuery);
        List<Tuple> list = jpqlQuery.fetch();

        List<User> userList = new ArrayList<>();
        List<UserProfile> userProfileList = new ArrayList<>();
        list.forEach(tuple -> {
            Optional.ofNullable(tuple.get(qUser)).ifPresent(userList::add);
            Optional.ofNullable(tuple.get(qUserProfile)).ifPresent(userProfileList::add);
        });
        Assertions.assertEquals(1, fetchCount);
        Assertions.assertEquals(1, userList.size());
        Assertions.assertEquals(1, userProfileList.size());
    }

    @Test
    void pageByExists() {
        QUser qUser = QUser.user;
        QUserProfile qUserProfile = QUserProfile.userProfile;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qUser.username.eq("icuxika"));
        booleanBuilder.and(
                JPAExpressions
                        .select(qUserProfile)
                        .from(qUserProfile)
                        .where(qUser.id.eq(qUserProfile.userId))
                        .exists()
        );
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());
        Page<User> userPage = userRepository.findAll(booleanBuilder, pageable);
        Assertions.assertEquals(1, userPage.getTotalElements());
    }
}