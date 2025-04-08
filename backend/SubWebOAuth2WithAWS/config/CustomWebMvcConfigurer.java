package com.kcj.SubWebOAuth2WithAWS.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/genre/**") //URL 패턴
                .addResourceLocations("file:C:/SubWeb_ver1/SubWeb/genre/"); //실제 파일 경로
        //URL 패턴의 경로와 실제 파일 경로를 지정해줘야 서버에서 이미지를 받아올 수 있음
    }
}
