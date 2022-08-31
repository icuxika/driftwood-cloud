package com.icuxika.admin.entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Oauth2AuthorizationConsentId implements Serializable {
    private static final long serialVersionUID = -4420876538252498971L;
    @Column(name = "registered_client_id", nullable = false, length = 100)
    private String registeredClientId;
    @Column(name = "principal_name", nullable = false, length = 200)
    private String principalName;

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getRegisteredClientId() {
        return registeredClientId;
    }

    public void setRegisteredClientId(String registeredClientId) {
        this.registeredClientId = registeredClientId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(registeredClientId, principalName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Oauth2AuthorizationConsentId entity = (Oauth2AuthorizationConsentId) o;
        return Objects.equals(this.registeredClientId, entity.registeredClientId) &&
                Objects.equals(this.principalName, entity.principalName);
    }
}