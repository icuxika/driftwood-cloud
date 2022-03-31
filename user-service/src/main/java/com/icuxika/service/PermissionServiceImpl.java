package com.icuxika.service;

import com.icuxika.repository.PermissionRepository;
import com.icuxika.user.entity.Permission;
import com.icuxika.util.BeanExUtil;
import com.icuxika.util.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Page<Permission> getPage(Pageable pageable, Permission permission) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("authority", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Permission> example = Example.of(permission, matcher);
        return permissionRepository.findAll(example, pageable);
    }

    @Override
    public List<Permission> getList(Permission permission) {
        return permissionRepository.findAll(Example.of(permission));
    }

    @Override
    public Permission getById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Permission permission) {
        permissionRepository.findByName(permission.getName()).ifPresent(p -> {
            throw new RuntimeException("权限名已被使用");
        });

        Long currentUserId = SecurityUtil.getUserId();
        permission.setCreateTime(LocalDateTime.now());
        permission.setCreateUserId(currentUserId);
        permission.setUpdateTime(LocalDateTime.now());
        permission.setUpdateUserId(currentUserId);
        permissionRepository.save(permission);
    }

    @Override
    public void update(Permission permission) {
        Permission exist = permissionRepository.findById(permission.getId()).orElseThrow(() -> new RuntimeException("数据不存在"));
        BeanUtils.copyProperties(permission, exist, BeanExUtil.getIgnorePropertyArray(permission));
        exist.setUpdateTime(LocalDateTime.now());
        exist.setUpdateUserId(SecurityUtil.getUserId());
        permissionRepository.save(exist);
    }

    @Override
    public void deleteById(Long id) {
        if (permissionRepository.existsById(id)) {
            permissionRepository.deleteById(id);
        }
    }
}
