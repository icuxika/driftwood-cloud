package com.icuxika.admin.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth2_authorization")
public class Oauth2Authorization {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "registered_client_id", nullable = false, length = 100)
    private String registeredClientId;

    @Column(name = "principal_name", nullable = false, length = 200)
    private String principalName;

    @Column(name = "authorization_grant_type", nullable = false, length = 100)
    private String authorizationGrantType;

    @Column(name = "authorized_scopes", nullable = true, length = 1000)
    private String authorizedScopes;

    @Lob
    @Column(name = "attributes", nullable = true, columnDefinition = "BLOB")
    private byte[] attributes;

    @Column(name = "state", nullable = true, length = 500)
    private String state;

    @Lob
    @Column(name = "authorization_code_value", nullable = true, columnDefinition = "BLOB")
    private byte[] authorizationCodeValue;

    @Column(name = "authorization_code_issued_at", nullable = true)
    private LocalDateTime authorizationCodeIssuedAt;

    @Column(name = "authorization_code_expires_at", nullable = true)
    private LocalDateTime authorizationCodeExpiresAt;

    @Lob
    @Column(name = "authorization_code_metadata", nullable = true, columnDefinition = "BLOB")
    private byte[] authorizationCodeMetadata;

    @Lob
    @Column(name = "access_token_value", nullable = true, columnDefinition = "BLOB")
    private byte[] accessTokenValue;

    @Column(name = "access_token_issued_at", nullable = true)
    private LocalDateTime accessTokenIssuedAt;

    @Column(name = "access_token_expires_at", nullable = true)
    private LocalDateTime accessTokenExpiresAt;

    @Lob
    @Column(name = "access_token_metadata", nullable = true, columnDefinition = "BLOB")
    private byte[] accessTokenMetadata;

    @Column(name = "access_token_type", nullable = true, length = 100)
    private String accessTokenType;

    @Column(name = "access_token_scopes", nullable = true, length = 1000)
    private String accessTokenScopes;

    @Lob
    @Column(name = "oidc_id_token_value", nullable = true, columnDefinition = "BLOB")
    private byte[] oidcIdTokenValue;

    @Column(name = "oidc_id_token_issued_at", nullable = true)
    private LocalDateTime oidcIdTokenIssuedAt;

    @Column(name = "oidc_id_token_expires_at", nullable = true)
    private LocalDateTime oidcIdTokenExpiresAt;

    @Lob
    @Column(name = "oidc_id_token_metadata", nullable = true, columnDefinition = "BLOB")
    private byte[] oidcIdTokenMetadata;

    @Lob
    @Column(name = "refresh_token_value", nullable = true, columnDefinition = "BLOB")
    private byte[] refreshTokenValue;

    @Column(name = "refresh_token_issued_at", nullable = true)
    private LocalDateTime refreshTokenIssuedAt;

    @Column(name = "refresh_token_expires_at", nullable = true)
    private LocalDateTime refreshTokenExpiresAt;

    @Lob
    @Column(name = "refresh_token_metadata", nullable = true, columnDefinition = "BLOB")
    private byte[] refreshTokenMetadata;

    @Lob
    @Column(name = "user_code_value", nullable = true, columnDefinition = "BLOB")
    private byte[] userCodeValue;

    @Column(name = "user_code_issued_at", nullable = true)
    private LocalDateTime userCodeIssuedAt;

    @Column(name = "user_code_expires_at", nullable = true)
    private LocalDateTime userCodeExpiresAt;

    @Lob
    @Column(name = "user_code_metadata", nullable = true, columnDefinition = "BLOB")
    private byte[] userCodeMetadata;

    @Lob
    @Column(name = "device_code_value", nullable = true, columnDefinition = "BLOB")
    private byte[] deviceCodeValue;

    @Column(name = "device_code_issued_at", nullable = true)
    private LocalDateTime deviceCodeIssuedAt;

    @Column(name = "device_code_expires_at", nullable = true)
    private LocalDateTime deviceCodeExpiresAt;

    @Lob
    @Column(name = "device_code_metadata", nullable = true, columnDefinition = "BLOB")
    private byte[] deviceCodeMetadata;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegisteredClientId() {
        return registeredClientId;
    }

    public void setRegisteredClientId(String registeredClientId) {
        this.registeredClientId = registeredClientId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public void setAuthorizationGrantType(String authorizationGrantType) {
        this.authorizationGrantType = authorizationGrantType;
    }

    public String getAuthorizedScopes() {
        return authorizedScopes;
    }

    public void setAuthorizedScopes(String authorizedScopes) {
        this.authorizedScopes = authorizedScopes;
    }

    public byte[] getAttributes() {
        return attributes;
    }

    public void setAttributes(byte[] attributes) {
        this.attributes = attributes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public byte[] getAuthorizationCodeValue() {
        return authorizationCodeValue;
    }

    public void setAuthorizationCodeValue(byte[] authorizationCodeValue) {
        this.authorizationCodeValue = authorizationCodeValue;
    }

    public LocalDateTime getAuthorizationCodeIssuedAt() {
        return authorizationCodeIssuedAt;
    }

    public void setAuthorizationCodeIssuedAt(LocalDateTime authorizationCodeIssuedAt) {
        this.authorizationCodeIssuedAt = authorizationCodeIssuedAt;
    }

    public LocalDateTime getAuthorizationCodeExpiresAt() {
        return authorizationCodeExpiresAt;
    }

    public void setAuthorizationCodeExpiresAt(LocalDateTime authorizationCodeExpiresAt) {
        this.authorizationCodeExpiresAt = authorizationCodeExpiresAt;
    }

    public byte[] getAuthorizationCodeMetadata() {
        return authorizationCodeMetadata;
    }

    public void setAuthorizationCodeMetadata(byte[] authorizationCodeMetadata) {
        this.authorizationCodeMetadata = authorizationCodeMetadata;
    }

    public byte[] getAccessTokenValue() {
        return accessTokenValue;
    }

    public void setAccessTokenValue(byte[] accessTokenValue) {
        this.accessTokenValue = accessTokenValue;
    }

    public LocalDateTime getAccessTokenIssuedAt() {
        return accessTokenIssuedAt;
    }

    public void setAccessTokenIssuedAt(LocalDateTime accessTokenIssuedAt) {
        this.accessTokenIssuedAt = accessTokenIssuedAt;
    }

    public LocalDateTime getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public void setAccessTokenExpiresAt(LocalDateTime accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public byte[] getAccessTokenMetadata() {
        return accessTokenMetadata;
    }

    public void setAccessTokenMetadata(byte[] accessTokenMetadata) {
        this.accessTokenMetadata = accessTokenMetadata;
    }

    public String getAccessTokenType() {
        return accessTokenType;
    }

    public void setAccessTokenType(String accessTokenType) {
        this.accessTokenType = accessTokenType;
    }

    public String getAccessTokenScopes() {
        return accessTokenScopes;
    }

    public void setAccessTokenScopes(String accessTokenScopes) {
        this.accessTokenScopes = accessTokenScopes;
    }

    public byte[] getOidcIdTokenValue() {
        return oidcIdTokenValue;
    }

    public void setOidcIdTokenValue(byte[] oidcIdTokenValue) {
        this.oidcIdTokenValue = oidcIdTokenValue;
    }

    public LocalDateTime getOidcIdTokenIssuedAt() {
        return oidcIdTokenIssuedAt;
    }

    public void setOidcIdTokenIssuedAt(LocalDateTime oidcIdTokenIssuedAt) {
        this.oidcIdTokenIssuedAt = oidcIdTokenIssuedAt;
    }

    public LocalDateTime getOidcIdTokenExpiresAt() {
        return oidcIdTokenExpiresAt;
    }

    public void setOidcIdTokenExpiresAt(LocalDateTime oidcIdTokenExpiresAt) {
        this.oidcIdTokenExpiresAt = oidcIdTokenExpiresAt;
    }

    public byte[] getOidcIdTokenMetadata() {
        return oidcIdTokenMetadata;
    }

    public void setOidcIdTokenMetadata(byte[] oidcIdTokenMetadata) {
        this.oidcIdTokenMetadata = oidcIdTokenMetadata;
    }

    public byte[] getRefreshTokenValue() {
        return refreshTokenValue;
    }

    public void setRefreshTokenValue(byte[] refreshTokenValue) {
        this.refreshTokenValue = refreshTokenValue;
    }

    public LocalDateTime getRefreshTokenIssuedAt() {
        return refreshTokenIssuedAt;
    }

    public void setRefreshTokenIssuedAt(LocalDateTime refreshTokenIssuedAt) {
        this.refreshTokenIssuedAt = refreshTokenIssuedAt;
    }

    public LocalDateTime getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

    public void setRefreshTokenExpiresAt(LocalDateTime refreshTokenExpiresAt) {
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public byte[] getRefreshTokenMetadata() {
        return refreshTokenMetadata;
    }

    public void setRefreshTokenMetadata(byte[] refreshTokenMetadata) {
        this.refreshTokenMetadata = refreshTokenMetadata;
    }

    public byte[] getUserCodeValue() {
        return userCodeValue;
    }

    public void setUserCodeValue(byte[] userCodeValue) {
        this.userCodeValue = userCodeValue;
    }

    public LocalDateTime getUserCodeIssuedAt() {
        return userCodeIssuedAt;
    }

    public void setUserCodeIssuedAt(LocalDateTime userCodeIssuedAt) {
        this.userCodeIssuedAt = userCodeIssuedAt;
    }

    public LocalDateTime getUserCodeExpiresAt() {
        return userCodeExpiresAt;
    }

    public void setUserCodeExpiresAt(LocalDateTime userCodeExpiresAt) {
        this.userCodeExpiresAt = userCodeExpiresAt;
    }

    public byte[] getUserCodeMetadata() {
        return userCodeMetadata;
    }

    public void setUserCodeMetadata(byte[] userCodeMetadata) {
        this.userCodeMetadata = userCodeMetadata;
    }

    public byte[] getDeviceCodeValue() {
        return deviceCodeValue;
    }

    public void setDeviceCodeValue(byte[] deviceCodeValue) {
        this.deviceCodeValue = deviceCodeValue;
    }

    public LocalDateTime getDeviceCodeIssuedAt() {
        return deviceCodeIssuedAt;
    }

    public void setDeviceCodeIssuedAt(LocalDateTime deviceCodeIssuedAt) {
        this.deviceCodeIssuedAt = deviceCodeIssuedAt;
    }

    public LocalDateTime getDeviceCodeExpiresAt() {
        return deviceCodeExpiresAt;
    }

    public void setDeviceCodeExpiresAt(LocalDateTime deviceCodeExpiresAt) {
        this.deviceCodeExpiresAt = deviceCodeExpiresAt;
    }

    public byte[] getDeviceCodeMetadata() {
        return deviceCodeMetadata;
    }

    public void setDeviceCodeMetadata(byte[] deviceCodeMetadata) {
        this.deviceCodeMetadata = deviceCodeMetadata;
    }

}