package com.icuxika.admin.dto;

import com.icuxika.admin.enumerate.SupportGrantType;
import com.icuxika.framework.basic.constant.ClientType;

public class LoginDTO {

    /**
     * 授权类型，应包含在 {@link SupportGrantType}，执行登录前应检查
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
     * 客户端类型，应包含在 {@link ClientType}，不检查
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
