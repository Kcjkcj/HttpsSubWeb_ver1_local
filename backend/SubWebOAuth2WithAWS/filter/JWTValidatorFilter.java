package com.kcj.SubWebOAuth2WithAWS.filter;

import com.kcj.SubWebOAuth2WithAWS.config.CustomUserDetails;
import com.kcj.SubWebOAuth2WithAWS.constants.ApplicationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class JWTValidatorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        //쿠키의 SameSite-Strict 정책 검증
        if(request.getHeader("Origin") !=null 
                && !request.getHeader("Origin").startsWith("https://localhost")) {
            throw new BadCredentialsException("Cross-site 요청 거부");
        }
        //String jwt = request.getHeader(ApplicationConstants.JWT_HEADER);
        Cookie[] cookies = request.getCookies();
        String jwt = null;
        if(cookies !=null){
            for (Cookie cookie : cookies){
                if("jwt".equals(cookie.getName())){
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        if(null != jwt){ //Bearer  접두사를 붙이는게 원칙
            try {
                Environment env = getEnvironment(); //환경변수 (core.env Generic Filter Bean)
                if (null != env) {
                    String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY, ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
                    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); //SHA
                    if (secretKey != null) {
                        Claims claims = Jwts.parser().verifyWith(secretKey) 
                                .build().parseSignedClaims(jwt).getPayload(); //생성기에서 만들때 서명된 비밀키를 사용
                        //build 과정에서 토큰의 변조가 검증됨. parser로 (헤더, 페이로드, 서명)으로 분리되며, 클레임 정보도 메타데이터를 통해 얻을 수 있음
                        int id = Integer.parseInt(claims.get("id").toString()); //jwt에 id를 받아야지
                        String username = String.valueOf(claims.get("email"));
                        String authorities = String.valueOf(claims.get("authorities"));

                        List<GrantedAuthority> grantedAuthorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                        CustomUserDetails userDetails = new CustomUserDetails(id,username,"",grantedAuthorityList);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, grantedAuthorityList);
                        //여기서 인증 객체를 userDetails으로 해야 Principal이 CustomUserDetails 객체로 저장이 됨.. 그래야만 JWT 생성기에서도 Principal을 CustomUserDetails로 인식가능
                        SecurityContextHolder.getContext().setAuthentication(authentication); //인증 객체에 id정보를 추가하면 컨트롤러에서 id값을 인식하고 통과/차단을 할 수 있음
                    }
                }
            } catch (Exception exception){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.setHeader("WWW-Authenticate", ""); // 👈 팝업 방지 핵심
                response.getWriter().write("{\"message\": \"Invalid Token\"}");
                log.info("JWT Exception 발생: {}", exception.getMessage());
                return; // ❗️반드시 리턴해야 필터체인이 계속 안 돈다
            }
        }
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        boolean excluded = path.startsWith("/api/register")
                || path.startsWith("/api/main")
                || path.startsWith("/api/user")
                || path.startsWith("/api/token")
                || path.startsWith("/error")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || "OPTIONS".equalsIgnoreCase(request.getMethod());

        log.info("[JWTValidatorFilter] path: {}, excluded: {}", path, excluded);
        log.info("JWTValidatorFilter invoked - path: {}, should filter: {}", path, !excluded);

        return excluded;

        //User API로 접속할 때만 JWT 검증 필터가 동작하지 않도록. 아직 로그인을 안했으니 토큰이 있을리 없기 때문에
    }
}
