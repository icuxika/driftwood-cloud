package com.icuxika.admin.vo;

import com.fasterxml.jackson.annotation.JsonAlias;

public class GithubUser {

    private String login;

    private String id;

    @JsonAlias("avatar_url")
    private String avatarUrl;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "GithubUser{" +
                "login='" + login + '\'' +
                ", id='" + id + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
