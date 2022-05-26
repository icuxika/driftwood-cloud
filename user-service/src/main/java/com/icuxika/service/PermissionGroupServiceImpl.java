package com.icuxika.service;

import com.icuxika.constant.SystemConstant;
import com.icuxika.modules.user.entity.PermissionGroup;
import com.icuxika.repository.PermissionGroupRepository;
import com.icuxika.util.BeanExUtil;
import com.icuxika.util.TreeNode;
import com.icuxika.util.TreeUtil;
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
            throw new RuntimeException("PermissionGroup with name " + permissionGroup.getName() + " already exists");
        });
        if (!SystemConstant.TREE_ROOT_ID.equals(permissionGroup.getParentId()) && permissionGroupRepository.findById(permissionGroup.getParentId()).isEmpty()) {
            throw new RuntimeException("Parent PermissionGroup not found");
        }

        permissionGroupRepository.save(permissionGroup);
    }

    @Override
    public void update(PermissionGroup permissionGroup) {
        PermissionGroup exist = permissionGroupRepository.findById(permissionGroup.getId()).orElseThrow(() -> new RuntimeException("PermissionGroup not found"));
        if (permissionGroup.getParentId() != null && !permissionGroup.getParentId().equals(exist.getParentId())) {
            if (permissionGroupRepository.findById(permissionGroup.getParentId()).isEmpty()) {
                throw new RuntimeException("Parent PermissionGroup not found");
            }
        }
        BeanUtils.copyProperties(permissionGroup, exist, BeanExUtil.getIgnorePropertyArray(permissionGroup));
        permissionGroupRepository.save(exist);
    }

    @Override
    public void deleteById(Long id) {
        if (permissionGroupRepository.existsById(id)) {
            if (!permissionGroupRepository.findByParentId(id).isEmpty()) {
                throw new RuntimeException("PermissionGroup has children, can't delete");
            }
            permissionGroupRepository.deleteById(id);
        }
    }

}
