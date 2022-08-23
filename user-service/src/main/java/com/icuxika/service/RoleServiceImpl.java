package com.icuxika.service;

import com.icuxika.exception.GlobalServiceException;
import com.icuxika.modules.user.dto.BindOneDTO;
import com.icuxika.modules.user.entity.Role;
import com.icuxika.modules.user.entity.RolePermission;
import com.icuxika.repository.PermissionRepository;
import com.icuxika.repository.RolePermissionRepository;
import com.icuxika.repository.RoleRepository;
import com.icuxika.repository.UserRoleRepository;
import com.icuxika.util.BeanExUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final UserRoleRepository userRoleRepository;

    private final RolePermissionRepository rolePermissionRepository;

    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRoleRepository userRoleRepository, RolePermissionRepository rolePermissionRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
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
            throw new GlobalServiceException("角色名已被使用");
        });

        roleRepository.save(role);
    }

    @Override
    public void update(Role role) {
        Role exist = roleRepository.findById(role.getId()).orElseThrow(() -> new GlobalServiceException("数据不存在"));
        BeanUtils.copyProperties(role, exist, BeanExUtil.getIgnorePropertyArray(role));
        roleRepository.save(exist);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
            // 删除用户与角色绑定
            userRoleRepository.deleteByRoleId(id);
            // 删除角色与权限绑定
            rolePermissionRepository.deleteByRoleId(id);
        }
    }

    @Override
    public void bindAuthorities(BindOneDTO bindOneDTO) {
        if (!roleRepository.existsById(bindOneDTO.getId())) {
            throw new GlobalServiceException("角色数据不存在");
        }
        if (permissionRepository.findByIdIn(bindOneDTO.getIdList()).size() != bindOneDTO.getIdList().size()) {
            throw new GlobalServiceException("权限数据不存在");
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
