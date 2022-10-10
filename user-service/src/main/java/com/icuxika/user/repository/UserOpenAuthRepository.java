package com.icuxika.user.repository;

import com.icuxika.framework.object.modules.user.entity.UserOpenAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOpenAuthRepository extends JpaRepository<UserOpenAuth, Long> {
}
