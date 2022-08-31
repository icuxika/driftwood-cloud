package com.icuxika.user.service;

import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.basic.util.BeanExUtil;
import com.icuxika.framework.basic.util.TreeNode;
import com.icuxika.framework.basic.util.TreeUtil;
import com.icuxika.framework.object.modules.user.entity.PermissionGroup;
import com.icuxika.user.repository.PermissionGroupRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionGroupServiceImpl implements PermissionGroupService {

    private final PermissionGroupRepository permissionGroupRepository;

    public PermissionGroupServiceImpl(PermissionGroupRepository permissionGroupRepository) {
        this.permissionGroupRepository = permissionGroupRepository;
    }

    @Override
    public Page<PermissionGroup> getPage(Pageable pageable, PermissionGroup permissionGroup) {
        Example<PermissionGroup> example = Example.of(permissionGroup);
        return permissionGroupRepository.findAll(example, pageable);
    }

    @Override
    public List<PermissionGroup> getList(PermissionGroup permissionGroup) {
        return permissionGroupRepository.findAll(Example.of(permissionGroup));
    }

    @Override
    public List<TreeNode<PermissionGroup>> getTree() {
        List<PermissionGroup> permissionGroups = permissionGroupRepository.findAll();
        return TreeUtil.buildTree(permissionGroups, PermissionGroup::getId, PermissionGroup::getParentId);
    }

    @Override
    public PermissionGroup getById(Long id) {
        return permissionGroupRepository.findById(id).orElse(null);
    }

    @Override
    public void save(PermissionGroup permissionGroup) {
        permissionGroupRepository.findByName(permissionGroup.getName()).ifPresent(exist -> {
            throw new GlobalServiceException("权限分组[" + permissionGroup.getName() + "]已经存在");
        });
        if (!SystemConstant.TREE_ROOT_ID.equals(permissionGroup.getParentId()) && permissionGroupRepository.findById(permissionGroup.getParentId()).isEmpty()) {
            throw new GlobalServiceException("父权限分组不存在");
        }

        permissionGroupRepository.save(permissionGroup);
    }

    @Override
    public void update(PermissionGroup permissionGroup) {
        PermissionGroup exist = permissionGroupRepository.findById(permissionGroup.getId()).orElseThrow(() -> new GlobalServiceException("权限分组不存在"));
        if (permissionGroup.getParentId() != null && !permissionGroup.getParentId().equals(exist.getParentId())) {
            if (permissionGroupRepository.findById(permissionGroup.getParentId()).isEmpty()) {
                throw new GlobalServiceException("父权限分组不存在");
            }
        }
        BeanUtils.copyProperties(permissionGroup, exist, BeanExUtil.getIgnorePropertyArray(permissionGroup));
        permissionGroupRepository.save(exist);
    }

    @Override
    public void deleteById(Long id) {
        if (permissionGroupRepository.existsById(id)) {
            if (!permissionGroupRepository.findByParentId(id).isEmpty()) {
                throw new GlobalServiceException("权限分组包含权限，无法删除");
            }
            permissionGroupRepository.deleteById(id);
        }
    }

}
