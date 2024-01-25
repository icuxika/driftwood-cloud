package com.icuxika.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.object.base.util.PageableUtil;
import com.icuxika.framework.object.modules.user.dto.BindOneDTO;
import com.icuxika.framework.object.modules.user.entity.Permission;
import com.icuxika.framework.object.modules.user.entity.Role;
import com.icuxika.framework.object.modules.user.entity.RolePermission;
import com.icuxika.framework.object.modules.user.entity.UserRole;
import com.icuxika.user.mapper.PermissionMapper;
import com.icuxika.user.mapper.RoleMapper;
import com.icuxika.user.mapper.RolePermissionMapper;
import com.icuxika.user.mapper.UserRoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "ext.model-persistence-type", havingValue = "MyBatis")
public class RoleServiceImplM implements RoleService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    public RoleServiceImplM(RoleMapper roleMapper, UserRoleMapper userRoleMapper, RolePermissionMapper rolePermissionMapper, PermissionMapper permissionMapper) {
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public Page<Role> getPage(Pageable pageable, Role role) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.hasText(role.getName()), Role::getName, role.getName())
                .like(StringUtils.hasText(role.getRole()), Role::getRole, role.getRole());

        var pageDTO = roleMapper.selectPage(PageableUtil.page(pageable), wrapper);
        return new PageImpl<>(pageDTO.getRecords(), pageable, pageDTO.getTotal());
    }

    @Override
    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public void save(Role role) {
        var exist = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getName, role.getName()));
        if (exist != null) {
            throw new GlobalServiceException("角色名已被使用");
        }
        roleMapper.insert(role);
    }

    @Override
    public void update(Role role) {
        var exist = roleMapper.selectById(role.getId());
        if (exist == null) {
            throw new GlobalServiceException("数据不存在");
        }
        BeanUtils.copyProperties(role, exist);
        // MyBatis-Plus 字段默认不更新NULL值
        roleMapper.updateById(exist);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(Long id) {
        var exist = roleMapper.selectById(id);
        if (exist == null) {
            throw new GlobalServiceException("数据不存在");
        }
        roleMapper.deleteById(exist);
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, id));
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bindAuthorities(BindOneDTO bindOneDTO) {
        var existRole = roleMapper.selectById(bindOneDTO.getId());
        if (existRole == null) {
            throw new GlobalServiceException("角色数据不存在");
        }
        long count = permissionMapper.selectCount(new LambdaQueryWrapper<Permission>().in(Permission::getId, bindOneDTO.getIdList()));
        if (count != bindOneDTO.getIdList().size()) {
            throw new GlobalServiceException("权限数据不存在");
        }
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, bindOneDTO.getId()));
        List<RolePermission> rolePermissionList = bindOneDTO.getIdList().stream().map(permissionId -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(bindOneDTO.getId());
            rolePermission.setPermissionId(permissionId);
            return rolePermission;
        }).collect(Collectors.toList());
        if (!rolePermissionList.isEmpty()) {
            rolePermissionMapper.insertBatchSomeColumn(rolePermissionList);
        }
    }
}
