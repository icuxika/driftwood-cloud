package com.icuxika.user.controller;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.util.TreeNode;
import com.icuxika.framework.object.modules.user.entity.PermissionGroup;
import com.icuxika.user.service.PermissionGroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissionGroup")
public class PermissionGroupController {

    private final PermissionGroupService permissionGroupService;

    public PermissionGroupController(PermissionGroupService permissionGroupService) {
        this.permissionGroupService = permissionGroupService;
    }

    @GetMapping("/page")
    public ApiData<Page<PermissionGroup>> getPage(@PageableDefault(sort = "id") Pageable pageable, PermissionGroup permissionGroup) {
        Page<PermissionGroup> page = permissionGroupService.getPage(pageable, permissionGroup);
        return ApiData.ok(page);
    }

    @GetMapping("/list")
    public ApiData<List<PermissionGroup>> getList(PermissionGroup permissionGroup) {
        List<PermissionGroup> permissionGroupList = permissionGroupService.getList(permissionGroup);
        return ApiData.ok(permissionGroupList);
    }

    @GetMapping("/tree")
    public ApiData<List<TreeNode<PermissionGroup>>> getTree() {
        List<TreeNode<PermissionGroup>> treeNodeList = permissionGroupService.getTree();
        return ApiData.ok(treeNodeList);
    }

    @GetMapping("/{id}")
    public ApiData<PermissionGroup> getById(@PathVariable("id") Long id) {
        PermissionGroup permissionGroup = permissionGroupService.getById(id);
        return ApiData.ok(permissionGroup);
    }

    @PostMapping
    public ApiData<PermissionGroup> save(@RequestBody PermissionGroup permissionGroup) {
        PermissionGroup newPermissionGroup = permissionGroupService.save(permissionGroup);
        return ApiData.ok(newPermissionGroup);
    }

    @PutMapping
    public ApiData<PermissionGroup> update(@RequestBody PermissionGroup permissionGroup) {
        PermissionGroup newPermissionGroup = permissionGroupService.update(permissionGroup);
        return ApiData.ok(newPermissionGroup);
    }

    @DeleteMapping("/{id}")
    public ApiData<Void> deleteById(@PathVariable("id") Long id) {
        permissionGroupService.deleteById(id);
        return ApiData.okMsg("删除成功");
    }

}
