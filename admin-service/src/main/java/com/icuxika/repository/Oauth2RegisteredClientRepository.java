package com.icuxika.repository;

import com.icuxika.entity.Oauth2RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface Oauth2RegisteredClientRepository extends JpaRepository<Oauth2RegisteredClient, String>, JpaSpecificationExecutor<Oauth2RegisteredClient> {
    Optional<Oauth2RegisteredClient> findByClientId(String clientId);
}
