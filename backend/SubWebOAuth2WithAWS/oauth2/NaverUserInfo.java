package com.kcj.SubWebOAuth2WithAWS.oauth2;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;
    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        Map<String,Object> naverAccount = (Map<String, Object>) attributes.get("response");
        if(naverAccount !=null){
            return (String)naverAccount.get("id");
        }
        return null;
    }

    @Override
    public String getEmail() {
        Map<String,Object> naverAccount = (Map<String, Object>) attributes.get("response");
        if(naverAccount !=null){
            return (String)naverAccount.get("email");
        }
        return null;
    }

    public String getName() {
        Map<String,Object> naverAccount = (Map<String, Object>) attributes.get("response");
        if(naverAccount !=null){
            return (String) naverAccount.get("name");
        }
        return null;
    }
}
