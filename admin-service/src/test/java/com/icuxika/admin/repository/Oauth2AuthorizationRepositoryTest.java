package com.icuxika.admin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Oauth2AuthorizationRepositoryTest {

    @Autowired
    private Oauth2AuthorizationRepository oauth2AuthorizationRepository;

    @Test
    void find() {
        oauth2AuthorizationRepository.findAll().forEach(oauth2Authorization -> {
            System.out.println(oauth2Authorization);
        });
    }
}