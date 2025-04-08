package com.kcj.SubWebOAuth2WithAWS.oauth2;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return (String)attributes.get("id");
    }

    @Override
    public String getEmail() {
        Map<String,Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if(kakaoAccount !=null){
            return (String)kakaoAccount.get("email");
        }
        return null;
    }

    public String getName() {
        Map<String,Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if(kakaoAccount !=null){
            Map<String,Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if(profile!=null){
                return (String)profile.get("nickname");
            }
        }
        return null;
    }
}
