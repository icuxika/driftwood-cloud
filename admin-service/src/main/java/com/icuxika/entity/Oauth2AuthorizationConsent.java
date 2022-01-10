package com.icuxika.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "oauth2_authorization_consent")
@Entity
public class Oauth2AuthorizationConsent {
    @EmbeddedId
    private Oauth2AuthorizationConsentId id;

    @Column(name = "authorities", nullable = false, length = 1000)
    private String authorities;

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public Oauth2AuthorizationConsentId getId() {
        return id;
    }

    public void setId(Oauth2AuthorizationConsentId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Oauth2AuthorizationConsent{" +
                "id=" + id +
                ", authorities='" + authorities + '\'' +
                '}';
    }
}