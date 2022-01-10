package com.icuxika.service;

import com.icuxika.entity.Oauth2RegisteredClient;
import org.springframework.data.domain.Page;

public interface Oauth2RegisteredClientService {
    Page<Oauth2RegisteredClient> getPage();
}
