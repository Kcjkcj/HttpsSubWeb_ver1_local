package com.kcj.SubWebOAuth2WithAWS.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomUserDetails extends User implements OAuth2User {
    private final int id;
    private Map<String, Object> attributes; // OAuth2 속성 저장 필드 추가
    public CustomUserDetails(int id, String username, String password,
                             Collection<? extends GrantedAuthority> authorities)
    {
        super(username,password,authorities);
        this.id = id;
    }

    //OAuth2User 인터페이스 오버라이딩
    @Override
    public Map<String,Object> getAttributes(){
        return this.attributes;
    }

    @Override
    public String getName(){
        return this.getUsername();
    }
    
    //OAuth2 속성 설정 메서드
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
