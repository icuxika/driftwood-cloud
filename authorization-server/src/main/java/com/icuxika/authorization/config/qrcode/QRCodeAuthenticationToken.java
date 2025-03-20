package com.icuxika.authorization.config.qrcode;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class QRCodeAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;

    public QRCodeAuthenticationToken(String token) {
        super(Collections.emptyList());
        this.token = token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

}
