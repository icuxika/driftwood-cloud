package com.icuxika.user.repository;

import com.icuxika.framework.object.modules.user.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByIdIn(@NonNull Collection<Long> ids);

    List<Menu> findByParentId(@NonNull Long parentId);

}
