package com.icuxika.service;

import com.icuxika.entity.Oauth2RegisteredClient;
import com.icuxika.repository.Oauth2RegisteredClientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class Oauth2RegisteredClientServiceImpl implements Oauth2RegisteredClientService {

    private final Oauth2RegisteredClientRepository oauth2RegisteredClientRepository;

    public Oauth2RegisteredClientServiceImpl(Oauth2RegisteredClientRepository oauth2RegisteredClientRepository) {
        this.oauth2RegisteredClientRepository = oauth2RegisteredClientRepository;
    }

    @Override
    public Page<Oauth2RegisteredClient> getPage() {
        return oauth2RegisteredClientRepository.findAll(PageRequest.of(0, 10));
    }
}
