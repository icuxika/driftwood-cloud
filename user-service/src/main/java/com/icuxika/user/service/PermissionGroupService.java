package com.icuxika.user.service;

import com.icuxika.framework.basic.util.TreeNode;
import com.icuxika.framework.object.modules.user.entity.PermissionGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionGroupService {

    Page<PermissionGroup> getPage(Pageable pageable, PermissionGroup permissionGroup);

    List<PermissionGroup> getList(PermissionGroup permissionGroup);

    List<TreeNode<PermissionGroup>> getTree();

    PermissionGroup getById(Long id);

    void save(PermissionGroup permissionGroup);

    void update(PermissionGroup permissionGroup);

    void deleteById(Long id);

}
