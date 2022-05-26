package com.icuxika.service;

import com.icuxika.modules.user.dto.BindOneDTO;
import com.icuxika.modules.user.entity.Role;
import com.icuxika.modules.user.entity.RolePermission;
import com.icuxika.repository.PermissionRepository;
import com.icuxika.repository.RolePermissionRepository;
import com.icuxika.repository.RoleRepository;
import com.icuxika.util.BeanExUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

        roleRepository.save(role);
    }

    @Override
    public void update(Role role) {
        Role exist = roleRepository.findById(role.getId()).orElseThrow(() -> new RuntimeException("数据不存在"));
        BeanUtils.copyProperties(role, exist, BeanExUtil.getIgnorePropertyArray(role));
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
        List<RolePermission> existList = rolePermissionRepository.findByPermissionIdIn(bindOneDTO.getIdList());
        List<RolePermission> rolePermissionList = bindOneDTO.getIdList().stream().map(permissionId -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(bindOneDTO.getId());
            rolePermission.setPermissionId(permissionId);
            existList.stream().filter(exist -> exist.getRoleId().equals(bindOneDTO.getId()) && exist.getPermissionId().equals(permissionId)).findFirst().ifPresent(exist -> {
                rolePermission.setId(exist.getId());
            });
            return rolePermission;
        }).collect(Collectors.toList());
        rolePermissionRepository.saveAll(rolePermissionList);
    }
}
