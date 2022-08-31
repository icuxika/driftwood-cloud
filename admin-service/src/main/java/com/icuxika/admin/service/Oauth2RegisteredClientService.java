package com.icuxika.admin.service;

import com.icuxika.admin.entity.Oauth2RegisteredClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Oauth2RegisteredClientService {
    Page<Oauth2RegisteredClient> getPage(Pageable pageable, Oauth2RegisteredClient oauth2RegisteredClient);

    Oauth2RegisteredClient getById(String id);

    void save(Oauth2RegisteredClient oauth2RegisteredClient);

    void update(Oauth2RegisteredClient oauth2RegisteredClient);

    void deleteById(String id);
}
