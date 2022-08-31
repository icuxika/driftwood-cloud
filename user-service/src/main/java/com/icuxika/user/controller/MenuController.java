package com.icuxika.user.controller;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.user.dto.BindOneDTO;
import com.icuxika.framework.object.modules.user.entity.Menu;
import com.icuxika.user.service.MenuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/page")
    public ApiData<Page<Menu>> getPage(@PageableDefault(sort = "id") Pageable pageable, Menu menu) {
        Page<Menu> page = menuService.getPage(pageable, menu);
        return ApiData.ok(page);
    }

    @GetMapping("/{id}")
    public ApiData<Menu> getById(@PathVariable("id") Long id) {
        Menu menu = menuService.getById(id);
        return ApiData.ok(menu);
    }

    @PostMapping
    public ApiData<Void> save(@RequestBody Menu menu) {
        menuService.save(menu);
        return ApiData.okMsg("新增成功");
    }

    @PutMapping
    public ApiData<Void> update(@RequestBody Menu menu) {
        menuService.update(menu);
        return ApiData.okMsg("更新成功");
    }

    @DeleteMapping("/{id}")
    public ApiData<Void> deleteById(@PathVariable("id") Long id) {
        menuService.deleteById(id);
        return ApiData.okMsg("删除成功");
    }

    @PostMapping("/bindAuthorities")
    public ApiData<Void> bindAuthorities(@RequestBody BindOneDTO bindOneDTO) {
        menuService.bindAuthorities(bindOneDTO);
        return ApiData.okMsg("绑定成功");
    }
}
