package com.kcj.SubWebOAuth2WithAWS.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//CsrfCookieFilter 클래스. OnceRequestFilter에서 확장

public class CsrfCookieFilter extends OncePerRequestFilter { //내부적으로 필터가 한 번만 동작하도록
    @Override
    protected  void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if(csrfToken !=null){
            boolean tokenExists = false;
            Cookie[] cookies = request.getCookies();

            if(cookies != null){ //발급된 csrf token을 확인하고 만료되지 않으면 새로 발급 X
                for(Cookie c : cookies){
                    if("XSRF-TOKEN".equals(c.getName()) && csrfToken.getToken().equals(c.getValue())) {
                        tokenExists = true;
                        break;
                    }
                }
            }
            if(!tokenExists) {
                Cookie cookie = new Cookie("XSRF-TOKEN", csrfToken.getToken());
                cookie.setPath("/");
                cookie.setSecure(true);
                cookie.setHttpOnly(false);
                cookie.setMaxAge(3600);
                cookie.setAttribute("SameSite", "Lax");
                response.addCookie(cookie);
            }
        }
        filterChain.doFilter(request,response);
    }
}
