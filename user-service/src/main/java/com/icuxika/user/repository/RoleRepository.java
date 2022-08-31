package com.icuxika.user.repository;

import com.icuxika.framework.object.modules.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByIdIn(@NonNull Collection<Long> ids);

    Optional<Role> findByName(@NonNull String name);

}
