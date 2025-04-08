package com.kcj.SubWebOAuth2WithAWS.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {
    
    @GetMapping("/api/csrf")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken){
        //필터에서 쿠키에 추가만 하기
        return ResponseEntity.ok().build();
    }
}
