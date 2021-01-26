package com.aac.kpi.gateway.auth.shiro.jwt;

import org.apache.shiro.authc.BearerToken;

public class JwtToken extends BearerToken {

    private static final long serialVersionUID = 1;

    public JwtToken(String token) {
        super(token);
    }

    public JwtToken(String token, String host) {
        super(token, host);
    }

    public JwtToken(BearerToken bearerToken){
        super(bearerToken.getToken(), bearerToken.getHost());
    }
}
