package com.icuxika.dto;

public class LoginDTO {

    /**
     * 授权类型，应包含在 {@link com.icuxika.enumerate.SupportGrantType}，执行登录前应检查
     */
    private String grantType;

    /**
     * 账户标识符（用户名、手机号等）
     */
    private String identifier;

    /**
     * 账户登录凭证（密码、短信验证码等）
     */
    private String credentials;

    /**
     * 客户端类型，应包含在 {@link com.icuxika.constant.ClientType}，不检查
     */
    private String clientType;

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}
