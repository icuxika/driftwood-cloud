package com.icuxika.repository;

import com.icuxika.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByIdIn(@NonNull Collection<Long> ids);

}
