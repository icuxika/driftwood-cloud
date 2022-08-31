package com.icuxika.admin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Oauth2AuthorizationConsentRepositoryTest {

    @Autowired
    private Oauth2AuthorizationConsentRepository oauth2AuthorizationConsentRepository;

    @Test
    void find() {
        oauth2AuthorizationConsentRepository.findAll().forEach(oauth2AuthorizationConsent -> {
            System.out.println(oauth2AuthorizationConsent);
        });
    }
}