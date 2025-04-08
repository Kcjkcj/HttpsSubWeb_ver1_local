package com.kcj.SubWebOAuth2WithAWS.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RequestValidationBeforeFilter implements Filter {
    //이 함수는 Basic Auth 요청 자체를 사전에 검증하기 위함, 보안 강화 및 요청 거부 기능
    //SpringSecurity는 Basic Auth 요청자체의 타당성을 검증하지 못함
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //Http 프로토콜이므로 위와같이 변경해줘야 함
        String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if(null !=header) {
            header = header.trim();
            if(StringUtils.startsWithIgnoreCase(header, "Basic ")){
                byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
                byte[] decoded;
                try {
                    decoded = Base64.getDecoder().decode(base64Token);
                    String token = new String(decoded,StandardCharsets.UTF_8); //username : pwd로 옴
                    int delim = token.indexOf(":");
                    if(delim ==-1) {
                        throw new BadCredentialsException("타당하지 않은 Basic 인증 토큰입니다.");
                    } 
                    String email = token.substring(0,delim);
                    if(email.toLowerCase().contains("test")){
                        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
                        return;
                    }
                }catch (IllegalArgumentException exception){
                    throw new BadCredentialsException("Basic 인증 토큰 Decode 실패");
                }

            }
        }
        chain.doFilter(request,response);
    }
}
