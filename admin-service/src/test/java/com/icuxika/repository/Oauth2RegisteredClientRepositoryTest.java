package com.icuxika.repository;

import com.icuxika.entity.Oauth2RegisteredClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class Oauth2RegisteredClientRepositoryTest {

    @Autowired
    private Oauth2RegisteredClientRepository oauth2RegisteredClientRepository;

    @Test
    void find() {
        oauth2RegisteredClientRepository.findAll().forEach(oauth2RegisteredClient -> {
            System.out.println(oauth2RegisteredClient);
        });
    }

    @Test
    void page() {
        Page<Oauth2RegisteredClient> page = oauth2RegisteredClientRepository.findAll(PageRequest.of(0, 10));
        System.out.println(page);
    }
}