package com.icuxika.user.controller;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.user.dto.BindOneDTO;
import com.icuxika.framework.object.modules.user.entity.Role;
import com.icuxika.user.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/page")
    public ApiData<Page<Role>> getPage(@PageableDefault(sort = "id") Pageable pageable, Role role) {
        Page<Role> page = roleService.getPage(pageable, role);
        return ApiData.ok(page);
    }

    @GetMapping("/{id}")
    public ApiData<Role> getById(@PathVariable("id") Long id) {
        Role role = roleService.getById(id);
        return ApiData.ok(role);
    }

    @PostMapping
    public ApiData<Void> save(@RequestBody Role role) {
        roleService.save(role);
        return ApiData.okMsg("新增成功");
    }

    @PutMapping
    public ApiData<Void> update(@RequestBody Role role) {
        roleService.update(role);
        return ApiData.okMsg("更新成功");
    }

    @DeleteMapping("/{id}")
    public ApiData<Void> deleteById(@PathVariable("id") Long id) {
        roleService.deleteById(id);
        return ApiData.okMsg("删除成功");
    }

    @PostMapping("/bindAuthorities")
    public ApiData<Void> bindAuthorities(@RequestBody BindOneDTO bindOneDTO) {
        roleService.bindAuthorities(bindOneDTO);
        return ApiData.okMsg("绑定成功");
    }
}
