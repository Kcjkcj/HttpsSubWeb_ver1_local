package com.kcj.SubWebOAuth2WithAWS.oauth2;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(
            String provider,
            Map<String, Object> attributes,
            String accessToken
    ) {
        return switch(provider.toLowerCase()){
            case "google" -> new GoogleUserInfo(attributes);
            case "github" -> new GithubUserInfo(attributes,accessToken);
            case "kakao" -> new KakaoUserInfo(attributes);
            case "naver" -> new NaverUserInfo(attributes);
            default -> throw new IllegalArgumentException("Unsupported provider");
        };
    }
}
