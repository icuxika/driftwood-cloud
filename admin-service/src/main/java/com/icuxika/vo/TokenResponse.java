package com.icuxika.vo;

import com.fasterxml.jackson.annotation.JsonAlias;

public class TokenResponse extends TokenInfo {

    @JsonAlias("error")
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
