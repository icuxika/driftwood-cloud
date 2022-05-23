package com.icuxika.repository;

import com.icuxika.modules.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(@NonNull Long userId);
}
