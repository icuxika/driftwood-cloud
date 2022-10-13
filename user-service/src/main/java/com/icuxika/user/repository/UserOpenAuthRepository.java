package com.icuxika.user.repository;

import com.icuxika.framework.object.modules.user.entity.UserOpenAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserOpenAuthRepository extends JpaRepository<UserOpenAuth, Long> {
    Optional<UserOpenAuth> findByOpenidAndType(@NonNull String openid, @NonNull Integer type);


}
