package com.icuxika.user.service;

import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.basic.util.BeanExUtil;
import com.icuxika.framework.basic.util.TreeNode;
import com.icuxika.framework.basic.util.TreeUtil;
import com.icuxika.framework.object.modules.user.dto.BindOneDTO;
import com.icuxika.framework.object.modules.user.entity.Menu;
import com.icuxika.framework.object.modules.user.entity.MenuPermission;
import com.icuxika.user.repository.MenuPermissionRepository;
import com.icuxika.user.repository.MenuRepository;
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
    public List<Menu> getList(Menu menu) {
        return menuRepository.findAll(Example.of(menu));
    }

    @Override
    public List<TreeNode<Menu>> getTree() {
        List<Menu> menus = menuRepository.findAll();
        return TreeUtil.buildTree(menus, Menu::getId, Menu::getParentId);
    }

    @Override
    public Menu getById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Menu menu) {
        if (!SystemConstant.TREE_ROOT_ID.equals(menu.getParentId()) && menuRepository.findById(menu.getParentId()).isEmpty()) {
            throw new GlobalServiceException("父菜单不存在");
        }
        menuRepository.save(menu);
    }

    @Override
    public void update(Menu menu) {
        Menu exist = menuRepository.findById(menu.getId()).orElseThrow(() -> new GlobalServiceException("数据不存在"));
        if (menu.getParentId() != null && !menu.getParentId().equals(exist.getParentId()) && !menu.getParentId().equals(SystemConstant.TREE_ROOT_ID)) {
            if (menuRepository.findById(menu.getParentId()).isEmpty()) {
                throw new GlobalServiceException("父菜单不存在");
            }
        }
        BeanUtils.copyProperties(menu, exist, BeanExUtil.getIgnorePropertyArray(menu));
        menuRepository.save(exist);
    }

    @Override
    public void deleteById(Long id) {
        if (menuRepository.existsById(id)) {
            if (!menuRepository.findByParentId(id).isEmpty()) {
                throw new GlobalServiceException("菜单下还包含子菜单，无法删除");
            }
            menuRepository.deleteById(id);
        }
    }

    @Override
    public void bindAuthorities(BindOneDTO bindOneDTO) {
        if (!menuRepository.existsById(bindOneDTO.getId())) {
            throw new GlobalServiceException("角色数据不存在");
        }
        if (menuPermissionRepository.findByIdIn(bindOneDTO.getIdList()).size() != bindOneDTO.getIdList().size()) {
            throw new GlobalServiceException("权限数据不存在");
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
