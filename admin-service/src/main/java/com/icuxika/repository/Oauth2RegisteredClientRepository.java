package com.icuxika.repository;

import com.icuxika.entity.Oauth2RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Oauth2RegisteredClientRepository extends JpaRepository<Oauth2RegisteredClient, String> {
}
