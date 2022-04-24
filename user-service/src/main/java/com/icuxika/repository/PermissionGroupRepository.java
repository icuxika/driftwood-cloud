package com.icuxika.repository;

import com.icuxika.modules.user.entity.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, Long> {

    Optional<PermissionGroup> findByName(@NonNull String name);

    List<PermissionGroup> findByParentId(@NonNull Long parentId);
}
