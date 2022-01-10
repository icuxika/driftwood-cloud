package com.icuxika.repository;

import com.icuxika.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserIdEquals(@NonNull Long userId);

}
