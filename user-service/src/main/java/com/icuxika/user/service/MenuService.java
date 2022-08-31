package com.icuxika.user.service;

import com.icuxika.framework.basic.util.TreeNode;
import com.icuxika.framework.object.modules.user.dto.BindOneDTO;
import com.icuxika.framework.object.modules.user.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MenuService {
    Page<Menu> getPage(Pageable pageable, Menu menu);

    List<Menu> getList(Menu menu);

    List<TreeNode<Menu>> getTree();

    Menu getById(Long id);

    void save(Menu menu);

    void update(Menu menu);

    void deleteById(Long id);

    void bindAuthorities(BindOneDTO bindOneDTO);

}
