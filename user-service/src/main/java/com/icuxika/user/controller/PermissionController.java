package com.icuxika.user.controller;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.user.entity.Permission;
import com.icuxika.user.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/page")
    public ApiData<Page<Permission>> getPage(@PageableDefault(sort = "id") Pageable pageable, Permission permission) {
        Page<Permission> page = permissionService.getPage(pageable, permission);
        return ApiData.ok(page);
    }

    @GetMapping("/list")
    public ApiData<List<Permission>> getList(Permission permission) {
        List<Permission> list = permissionService.getList(permission);
        return ApiData.ok(list);
    }

    @GetMapping("/{id}")
    public ApiData<Permission> getById(@PathVariable("id") Long id) {
        Permission permission = permissionService.getById(id);
        return ApiData.ok(permission);
    }

    @PostMapping
    public ApiData<Void> save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return ApiData.okMsg("新增成功");
    }

    @PutMapping
    public ApiData<Void> update(@RequestBody Permission permission) {
        permissionService.update(permission);
        return ApiData.okMsg("更新成功");
    }

    @DeleteMapping("/{id}")
    public ApiData<Void> deleteById(@PathVariable("id") Long id) {
        permissionService.deleteById(id);
        return ApiData.okMsg("删除成功");
    }

}
