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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StopWatch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest()
class UserRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CriteriaBuilderFactory criteriaBuilderFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

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

    @Test
    void page() {
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        User query = new User();
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<User> example = Example.of(query, matcher);
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());
        Page<User> userPage = userRepository.findAll(example, pageable);
        System.out.println("总数->" + userPage.getTotalElements());
        List<UserProfile> userProfileList = userProfileRepository.findByUserIdIn(userPage.getContent().stream().map(User::getId).toList());
        System.out.println("总数->" + userProfileList.size());
        stopWatch.stop();

        stopWatch.start();
        QUser qUser = QUser.user;
        JPQLQuery<User> userJPQLQuery = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                .select(qUser)
                .from(qUser);
        long fetchCount = userJPQLQuery.fetchCount();
        Querydsl querydsl = new Querydsl(entityManager, (new PathBuilderFactory()).create(User.class));
        userJPQLQuery = querydsl.applyPagination(pageable, userJPQLQuery);
        List<User> userList = userJPQLQuery.fetch();
        System.out.println("总数->" + fetchCount);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }

    @Test
    void generateUserData() {
        String url = "jdbc:mysql://127.0.0.1:3306/driftwood-cloud?serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&rewriteBatchedStatements=true";
        String username = "root";
        String password = "ALLURE_love921";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        long startId = 1000001L;
        long endId = startId + 100000L;

        long currentTimeMillis = System.currentTimeMillis();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);

            connection.setAutoCommit(false);

            String sql = """
                    insert into user(id,create_time,create_user_id,tenant_id,update_time,update_user_id,delete_time,deleted,is_account_non_expired,is_account_non_locked,is_credential_is_non_expired,is_enabled,nickname,password,phone,username) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                    """;
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < 100; i++) {
                String beginTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_TIME_PATTERN));
                while (startId < endId) {
                    preparedStatement.setLong(1, startId);
                    preparedStatement.setTimestamp(2, new Timestamp(currentTimeMillis));
                    preparedStatement.setLong(3, 1L);
                    preparedStatement.setString(4, SystemConstant.DEFAULT_TENANT_ID);
                    preparedStatement.setTimestamp(5, new Timestamp(currentTimeMillis));
                    preparedStatement.setLong(6, 1L);
                    preparedStatement.setTimestamp(7, null);
                    preparedStatement.setBoolean(8, false);
                    preparedStatement.setBoolean(9, true);
                    preparedStatement.setBoolean(10, true);
                    preparedStatement.setBoolean(11, true);
                    preparedStatement.setBoolean(12, true);
                    preparedStatement.setString(13, "nickname_" + startId);
                    preparedStatement.setString(14, "password_" + startId);
                    preparedStatement.setString(15, "phone_" + startId);
                    preparedStatement.setString(16, "username_" + startId);
                    preparedStatement.addBatch();
                    startId++;
                }
                preparedStatement.executeBatch();
                connection.commit();

                endId += 100000L;

                String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_TIME_PATTERN));
                System.out.println("第" + i + "批数据->[" + beginTime + "]" + " to [" + endTime + "]");
            }

            System.out.println("总耗时->" + (System.currentTimeMillis() - currentTimeMillis));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Test
    void generateUserProfileData() {
        String url = "jdbc:mysql://127.0.0.1:3306/driftwood-cloud?serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&rewriteBatchedStatements=true";
        String username = "root";
        String password = "ALLURE_love921";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        long startId = 1000001L;
        long endId = startId + 100000L;

        long currentTimeMillis = System.currentTimeMillis();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);

            connection.setAutoCommit(false);

            String sql = """
                    insert into user_profile (id, create_time, create_user_id, update_time, update_user_id, avatar, birthday, city, district, gender, last_remote_address, nation, province, remote_address, signature, street, street_number, user_id, tenant_id, avatar_file_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                    """;
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < 100; i++) {
                String beginTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_TIME_PATTERN));
                while (startId < endId) {
                    preparedStatement.setLong(1, startId);
                    preparedStatement.setTimestamp(2, new Timestamp(currentTimeMillis));
                    preparedStatement.setLong(3, 1L);
                    preparedStatement.setTimestamp(4, new Timestamp(currentTimeMillis));
                    preparedStatement.setLong(5, 1L);
                    preparedStatement.setString(6, null);
                    preparedStatement.setTimestamp(7, new Timestamp(currentTimeMillis));
                    preparedStatement.setString(8, null);
                    preparedStatement.setString(9, null);
                    preparedStatement.setInt(10, 1);
                    preparedStatement.setString(11, null);
                    preparedStatement.setString(12, null);
                    preparedStatement.setString(13, null);
                    preparedStatement.setString(14, null);
                    preparedStatement.setString(15, null);
                    preparedStatement.setString(16, null);
                    preparedStatement.setString(17, null);
                    preparedStatement.setLong(18, startId);
                    preparedStatement.setString(19, SystemConstant.DEFAULT_TENANT_ID);
                    preparedStatement.setLong(20, 1L);
                    preparedStatement.addBatch();
                    startId++;
                }
                preparedStatement.executeBatch();
                connection.commit();

                endId += 100000L;

                String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_TIME_PATTERN));
                System.out.println("第" + i + "批数据->[" + beginTime + "]" + " to [" + endTime + "]");
            }

            System.out.println("总耗时->" + (System.currentTimeMillis() - currentTimeMillis));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}