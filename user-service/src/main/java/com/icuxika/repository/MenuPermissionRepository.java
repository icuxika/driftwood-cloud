package com.icuxika.repository;

import com.icuxika.modules.user.entity.MenuPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

public interface MenuPermissionRepository extends JpaRepository<MenuPermission, Long> {

    List<MenuPermission> findByIdIn(@NonNull Collection<Long> ids);

    List<MenuPermission> findByPermissionIdIn(@NonNull Collection<Long> permissionIds);
}
