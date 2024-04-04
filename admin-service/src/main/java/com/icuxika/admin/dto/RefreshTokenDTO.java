package com.icuxika.admin.dto;

public class RefreshTokenDTO {

    private String loginGrantType;

    private String grantType;

    private String refreshToken;

    private String clientType;

    public String getLoginGrantType() {
        return loginGrantType;
    }

    public void setLoginGrantType(String loginGrantType) {
        this.loginGrantType = loginGrantType;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}
