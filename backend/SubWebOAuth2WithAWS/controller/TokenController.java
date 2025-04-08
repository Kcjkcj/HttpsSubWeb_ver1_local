package com.kcj.SubWebOAuth2WithAWS.controller;

import com.kcj.SubWebOAuth2WithAWS.config.CustomUserDetails;
import com.kcj.SubWebOAuth2WithAWS.constants.ApplicationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TokenController {
    private final Environment env;
    private String username;
    private int id;
    private String authorities;
    @PostMapping("/api/token")
    public ResponseEntity<?> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        if(!isValid(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid refresh Token");
        }

        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY, ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        //HMAC-SHA 메시지의 무결성과 인증을 보장, 비밀 키를 사용해 암호화, 해시값 생성, 메시지 변조 여부 파악 가능
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(new Date());

        String jwt = Jwts.builder().issuer("SubWeb").subject("JWT") //subweb에서 발행한 JWT
                .claim("id", id)
                .claim("email", username)
                .claim("authorities", authorities) //권한 String으로 받아서 ,로 나누기
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + 30000000)) //밀리초
                .signWith(secretKey).compact(); //서명을 해싱된 비밀키로 하는것 그렇기에 jwt 변조여부를 쉽게 알 수 있음

        Cookie accessCookie = new Cookie("jwt", jwt);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600); // 10분
        accessCookie.setAttribute("SameSite", "None");

        Cookie newRefreshToken = new Cookie("refreshToken", jwt);
        newRefreshToken.setHttpOnly(true);
        newRefreshToken.setSecure(true);
        newRefreshToken.setPath("/");
        newRefreshToken.setMaxAge(60*60*24*7); // 10분
        newRefreshToken.setAttribute("SameSite", "None");

        return ResponseEntity.ok()
                .header("Set-Cookie",accessCookie.toString())
                .header("Set-Cookie",newRefreshToken.toString())
                .build();
    }

    private boolean isValid(String refreshToken) {
        try {
            String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY, ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); //SHA

            Claims claims = Jwts.parser().verifyWith(secretKey)
                    .build().parseSignedClaims(refreshToken).getPayload(); //생성기에서 만들때 서명된 비밀키를 사용
            //build 과정에서 토큰의 변조가 검증됨. parser로 (헤더, 페이로드, 서명)으로 분리되며, 클레임 정보도 메타데이터를 통해 얻을 수 있음
            id = Integer.parseInt(claims.get("id").toString()); //jwt에 id를 받아야지
            username = String.valueOf(claims.get("email"));
            authorities = String.valueOf(claims.get("authorities"));
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }

    }
}
