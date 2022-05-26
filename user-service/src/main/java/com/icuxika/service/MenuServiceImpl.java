package com.icuxika.service;

import com.icuxika.modules.user.dto.BindOneDTO;
import com.icuxika.modules.user.entity.Menu;
import com.icuxika.modules.user.entity.MenuPermission;
import com.icuxika.repository.MenuPermissionRepository;
import com.icuxika.repository.MenuRepository;
import com.icuxika.util.BeanExUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    private final MenuPermissionRepository menuPermissionRepository;

    public MenuServiceImpl(MenuRepository menuRepository, MenuPermissionRepository menuPermissionRepository) {
        this.menuRepository = menuRepository;
        this.menuPermissionRepository = menuPermissionRepository;
    }

    @Override
    public Page<Menu> getPage(Pageable pageable, Menu menu) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("path", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Menu> example = Example.of(menu, matcher);
        return menuRepository.findAll(example, pageable);
    }

    @Override
    public Menu getById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Menu menu) {
        menuRepository.save(menu);
    }

    @Override
    public void update(Menu menu) {
        Menu exist = menuRepository.findById(menu.getId()).orElseThrow(() -> new RuntimeException("数据不存在"));
        BeanUtils.copyProperties(menu, exist, BeanExUtil.getIgnorePropertyArray(menu));
        menuRepository.save(exist);
    }

    @Override
    public void deleteById(Long id) {
        if (menuRepository.existsById(id)) {
            menuRepository.deleteById(id);
        }
    }

    @Override
    public void bindAuthorities(BindOneDTO bindOneDTO) {
        if (!menuRepository.existsById(bindOneDTO.getId())) {
            throw new RuntimeException("角色数据不存在");
        }
        if (menuPermissionRepository.findByIdIn(bindOneDTO.getIdList()).size() != bindOneDTO.getIdList().size()) {
            throw new RuntimeException("权限数据不存在");
        }
        List<MenuPermission> existList = menuPermissionRepository.findByPermissionIdIn(bindOneDTO.getIdList());
        List<MenuPermission> menuPermissionList = bindOneDTO.getIdList().stream().map(permissionId -> {
            MenuPermission menuPermission = new MenuPermission();
            menuPermission.setMenuId(bindOneDTO.getId());
            menuPermission.setPermissionId(permissionId);
            existList.stream().filter(exist -> exist.getMenuId().equals(bindOneDTO.getId()) && exist.getPermissionId().equals(permissionId)).findFirst().ifPresent(exist -> {
                menuPermission.setId(exist.getId());
            });
            return menuPermission;
        }).collect(Collectors.toList());
        menuPermissionRepository.saveAll(menuPermissionList);
    }
}
