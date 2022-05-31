package com.icuxika.repository;

import com.icuxika.common.DeletableRepository;
import com.icuxika.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, DeletableRepository<User, Long>, QuerydslPredicateExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPhone(String phone);

}