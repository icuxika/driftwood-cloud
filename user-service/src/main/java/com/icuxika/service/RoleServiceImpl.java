package com.icuxika.service;

import com.icuxika.modules.user.dto.BindOneDTO;
import com.icuxika.modules.user.entity.Role;
import com.icuxika.modules.user.entity.RolePermission;
import com.icuxika.repository.PermissionRepository;
import com.icuxika.repository.RolePermissionRepository;
import com.icuxika.repository.RoleRepository;
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
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final RolePermissionRepository rolePermissionRepository;

    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Page<Role> getPage(Pageable pageable, Role role) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("role", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Role> example = Example.of(role, matcher);
        return roleRepository.findAll(example, pageable);
    }

    @Override
    public Role getById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Role role) {
        roleRepository.findByName(role.getName()).ifPresent(exist -> {
            throw new RuntimeException("角色名已被使用");
        });

        Long currentUserId = SecurityUtil.getUserId();
        role.setCreateTime(LocalDateTime.now());
        role.setCreateUserId(currentUserId);
        role.setUpdateTime(LocalDateTime.now());
        role.setUpdateUserId(currentUserId);
        roleRepository.save(role);
    }

    @Override
    public void update(Role role) {
        Role exist = roleRepository.findById(role.getId()).orElseThrow(() -> new RuntimeException("数据不存在"));
        BeanUtils.copyProperties(role, exist, BeanExUtil.getIgnorePropertyArray(role));
        exist.setUpdateTime(LocalDateTime.now());
        exist.setUpdateUserId(SecurityUtil.getUserId());
        roleRepository.save(exist);
    }

    @Override
    public void deleteById(Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
        }
    }

    @Override
    public void bindAuthorities(BindOneDTO bindOneDTO) {
        if (!roleRepository.existsById(bindOneDTO.getId())) {
            throw new RuntimeException("角色数据不存在");
        }
        if (permissionRepository.findByIdIn(bindOneDTO.getIdList()).size() != bindOneDTO.getIdList().size()) {
            throw new RuntimeException("权限数据不存在");
        }
        Long currentUserId = SecurityUtil.getUserId();
        List<RolePermission> existList = rolePermissionRepository.findByPermissionIdIn(bindOneDTO.getIdList());
        List<RolePermission> rolePermissionList = bindOneDTO.getIdList().stream().map(permissionId -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(bindOneDTO.getId());
            rolePermission.setPermissionId(permissionId);
            rolePermission.setCreateTime(LocalDateTime.now());
            rolePermission.setCreateUserId(currentUserId);
            rolePermission.setUpdateTime(LocalDateTime.now());
            rolePermission.setUpdateUserId(currentUserId);
            existList.stream().filter(exist -> exist.getRoleId().equals(bindOneDTO.getId()) && exist.getPermissionId().equals(permissionId)).findFirst().ifPresent(exist -> {
                rolePermission.setId(exist.getId());
                rolePermission.setCreateTime(exist.getCreateTime());
                rolePermission.setCreateUserId(exist.getCreateUserId());
                rolePermission.setUpdateTime(LocalDateTime.now());
                rolePermission.setUpdateUserId(currentUserId);
            });
            return rolePermission;
        }).collect(Collectors.toList());
        rolePermissionRepository.saveAll(rolePermissionList);
    }
}
