package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.modules.user.entity.PermissionGroup;
import com.icuxika.service.PermissionGroupService;
import com.icuxika.util.TreeNode;
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
    public ApiData<Void> save(@RequestBody PermissionGroup permissionGroup) {
        permissionGroupService.save(permissionGroup);
        return ApiData.okMsg("保存成功");
    }

    @PutMapping
    public ApiData<Void> update(@RequestBody PermissionGroup permissionGroup) {
        permissionGroupService.update(permissionGroup);
        return ApiData.okMsg("更新成功");
    }

    @DeleteMapping("/{id}")
    public ApiData<Void> deleteById(@PathVariable("id") Long id) {
        permissionGroupService.deleteById(id);
        return ApiData.okMsg("删除成功");
    }

}
