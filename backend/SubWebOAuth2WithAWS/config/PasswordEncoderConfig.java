package com.kcj.SubWebOAuth2WithAWS.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() //설정 클래스에서 bean으로 설정해줘야 PasswordEncoder 사용가능
    {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
