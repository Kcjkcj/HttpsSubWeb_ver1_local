package com.kcj.SubWebOAuth2WithAWS.oauth2;

public interface OAuth2UserInfo {
    String getProvider(); //플랫폼 식별자 (Google, Github)
    String getProviderId(); //SNS 제공 자용자 id
    String getEmail();

}
