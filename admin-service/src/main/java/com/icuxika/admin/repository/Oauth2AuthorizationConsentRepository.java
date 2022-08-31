package com.icuxika.admin.repository;

import com.icuxika.admin.entity.Oauth2AuthorizationConsent;
import com.icuxika.admin.entity.Oauth2AuthorizationConsentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Oauth2AuthorizationConsentRepository extends JpaRepository<Oauth2AuthorizationConsent, Oauth2AuthorizationConsentId> {
}
