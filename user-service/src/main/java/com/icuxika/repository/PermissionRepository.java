package com.icuxika.repository;

import com.icuxika.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByIdIn(@NonNull Collection<Long> ids);
}
