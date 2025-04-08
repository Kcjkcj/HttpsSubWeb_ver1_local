package com.kcj.SubWebOAuth2WithAWS.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;

public class CustomOidcUser extends DefaultOidcUser {
    private final int accountId;

    public CustomOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, int accountId){
        super(authorities,idToken);
        this.accountId = accountId;
    }

    public int getAccountId() {
        return accountId;
    }
}
