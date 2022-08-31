package com.icuxika.admin.service;

import com.icuxika.admin.entity.Oauth2RegisteredClient;
import com.icuxika.admin.repository.Oauth2RegisteredClientRepository;
import com.icuxika.framework.basic.util.BeanExUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class Oauth2RegisteredClientServiceImpl implements Oauth2RegisteredClientService {

    private final Oauth2RegisteredClientRepository oauth2RegisteredClientRepository;

    public Oauth2RegisteredClientServiceImpl(Oauth2RegisteredClientRepository oauth2RegisteredClientRepository) {
        this.oauth2RegisteredClientRepository = oauth2RegisteredClientRepository;
    }

    @Override
    public Page<Oauth2RegisteredClient> getPage(Pageable pageable, Oauth2RegisteredClient oauth2RegisteredClient) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("clientId", ExampleMatcher.GenericPropertyMatchers.startsWith())
                .withMatcher("clientName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("authorizationGrantTypes", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Oauth2RegisteredClient> example = Example.of(oauth2RegisteredClient, matcher);
        return oauth2RegisteredClientRepository.findAll(example, pageable);
    }

    @Override
    public Oauth2RegisteredClient getById(String id) {
        return oauth2RegisteredClientRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Oauth2RegisteredClient oauth2RegisteredClient) {
        oauth2RegisteredClientRepository.findByClientId(oauth2RegisteredClient.getClientId()).ifPresent(exist -> {
            throw new RuntimeException("clientId已存在");
        });
        oauth2RegisteredClientRepository.save(oauth2RegisteredClient);
    }

    @Override
    public void update(Oauth2RegisteredClient oauth2RegisteredClient) {
        Oauth2RegisteredClient exist = oauth2RegisteredClientRepository.findById(oauth2RegisteredClient.getId()).orElseThrow(() -> new RuntimeException("数据不存在"));
        BeanUtils.copyProperties(oauth2RegisteredClient, exist, BeanExUtil.getIgnorePropertyArray(oauth2RegisteredClient));
        oauth2RegisteredClientRepository.save(exist);
    }

    @Override
    public void deleteById(String id) {
        if (oauth2RegisteredClientRepository.existsById(id)) {
            oauth2RegisteredClientRepository.deleteById(id);
        }
    }
}
