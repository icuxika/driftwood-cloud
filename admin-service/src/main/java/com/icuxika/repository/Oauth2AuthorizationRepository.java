package com.icuxika.repository;

import com.icuxika.entity.Oauth2Authorization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Oauth2AuthorizationRepository extends JpaRepository<Oauth2Authorization, String> {
}
