package com.icuxika.admin.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubAccessToken {

    @JsonAlias("access_token")
    private String accessToken;

    @JsonAlias("scope")
    private String scope;

    @JsonAlias("token_type")
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public String toString() {
        return "GithubAccessToken{" +
                "accessToken='" + accessToken + '\'' +
                ", scope='" + scope + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}
