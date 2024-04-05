package com.icuxika.user.service;

import com.icuxika.framework.object.modules.user.dto.AllPermissionDTO;
import com.icuxika.framework.object.modules.user.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {
    Page<Permission> getPage(Pageable pageable, Permission permission);

    List<Permission> getList(Permission permission);

    Permission getById(Long id);

    Permission save(Permission permission);

    Permission update(Permission permission);

    void deleteById(Long id);

    void updateAllPermission(AllPermissionDTO allPermissionDTO);
}
