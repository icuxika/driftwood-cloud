package com.icuxika.service;

import com.icuxika.user.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {
    Page<Permission> getPage(Pageable pageable, Permission permission);

    List<Permission> getList(Permission permission);

    Permission getById(Long id);

    void save(Permission permission);

    void update(Permission permission);

    void deleteById(Long id);

}
