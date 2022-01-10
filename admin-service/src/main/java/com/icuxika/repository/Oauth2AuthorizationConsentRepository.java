package com.icuxika.repository;

import com.icuxika.entity.Oauth2AuthorizationConsent;
import com.icuxika.entity.Oauth2AuthorizationConsentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Oauth2AuthorizationConsentRepository extends JpaRepository<Oauth2AuthorizationConsent, Oauth2AuthorizationConsentId> {
}
