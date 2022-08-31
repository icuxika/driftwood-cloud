package com.icuxika.user.service;

import com.icuxika.framework.object.modules.user.dto.BindOneDTO;
import com.icuxika.framework.object.modules.user.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuService {
    Page<Menu> getPage(Pageable pageable, Menu menu);

    Menu getById(Long id);

    void save(Menu menu);

    void update(Menu menu);

    void deleteById(Long id);

    void bindAuthorities(BindOneDTO bindOneDTO);
}
