package com.icuxika.user.repository;

import com.icuxika.framework.object.modules.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserId(@NonNull Long userId);

    List<UserRole> findByRoleIdIn(Collection<Long> roleIds);

    long deleteByRoleId(@NonNull Long roleId);

    long deleteByRoleIdIn(Collection<Long> roleIds);

}
