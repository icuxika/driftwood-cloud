package com.icuxika.user.service;

import com.icuxika.framework.object.modules.user.dto.BindOneDTO;
import com.icuxika.framework.object.modules.user.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    Page<Role> getPage(Pageable pageable, Role role);

    Role getById(Long id);

    void save(Role role);

    void update(Role role);

    void deleteById(Long id);

    void bindAuthorities(BindOneDTO bindOneDTO);
}
