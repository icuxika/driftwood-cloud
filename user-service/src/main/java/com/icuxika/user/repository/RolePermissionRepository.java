package com.icuxika.user.repository;

import com.icuxika.framework.object.modules.user.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRoleId(@NonNull Long roleId);

    List<RolePermission> findByPermissionId(@NonNull Long permissionId);

    List<RolePermission> findByPermissionIdIn(@NonNull Collection<Long> permissionIds);

    List<RolePermission> findByRoleIdIn(@NonNull Collection<Long> roleIds);

    long deleteByRoleId(@NonNull Long roleId);

    long deleteByRoleIdIn(Collection<Long> roleIds);

}
