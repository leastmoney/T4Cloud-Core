package com.t4cloud.t.base.authc.config;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 自定义TOKEN
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 21:32
 */
public class JwtToken implements AuthenticationToken {

    private static final long serialVersionUID = 1L;
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
