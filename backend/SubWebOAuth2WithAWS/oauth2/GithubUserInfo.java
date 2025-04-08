package com.kcj.SubWebOAuth2WithAWS.oauth2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;

public class GithubUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;
    private final String accessToken;

    public GithubUserInfo(Map<String, Object> attributes, String accessToken) {
        this.attributes = attributes;
        this.accessToken = accessToken;
    }


    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    public String getProviderId() {
        return (String)attributes.get("id");
    }

    public String getName(){
        Map<String,Object> userAttributes = (Map<String, Object>) attributes.get("User Attributes");
        if(userAttributes !=null){
            String nickname = (String) userAttributes.get("login");
            return nickname;
        }
        return null;
    }

    @Override
    public String getEmail() {
        return getPrimaryEmailFromGitHubAPI();
    }

    private String getPrimaryEmailFromGitHubAPI(){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map[]>response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                Map[].class
        );

        return Arrays.stream(response.getBody())
                .filter(email -> (Boolean) email.get("primary"))
                .findFirst()
                .map(email -> (String) email.get("email"))
                .orElseThrow(() -> new RuntimeException("Primary email not found"));
    }

}
