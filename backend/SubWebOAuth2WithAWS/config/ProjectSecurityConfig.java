package com.kcj.SubWebOAuth2WithAWS.config;


import com.kcj.SubWebOAuth2WithAWS.Service.CustomOAuth2UserService;
import com.kcj.SubWebOAuth2WithAWS.Service.CustomOidcUserService;
import com.kcj.SubWebOAuth2WithAWS.constants.ApplicationConstants;
import com.kcj.SubWebOAuth2WithAWS.exceptionhandling.CustomAccessDeniedHandler;
import com.kcj.SubWebOAuth2WithAWS.exceptionhandling.CustomAuthenticationEntryPoint;
import com.kcj.SubWebOAuth2WithAWS.filter.CsrfCookieFilter;
import com.kcj.SubWebOAuth2WithAWS.filter.JWTGeneratorFilter;
import com.kcj.SubWebOAuth2WithAWS.filter.JWTValidatorFilter;
import com.kcj.SubWebOAuth2WithAWS.filter.RequestValidationBeforeFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity // SecurityFilterChain을 제대로 적용하기 위해 3.x 부터는 필요할 수도 있음..
public class ProjectSecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final Environment env;
    private final JWTGeneratorFilter jwtGeneratorFilter;
    private final JWTValidatorFilter jwtValidatorFilter;
    public ProjectSecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomOidcUserService customOidcUserService
            , Environment env,
    JWTGeneratorFilter jwtGeneratorFilter, JWTValidatorFilter jwtValidatorFilter) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOidcUserService = customOidcUserService;
        this.env = env;
        this.jwtGeneratorFilter = jwtGeneratorFilter;
        this.jwtValidatorFilter = jwtValidatorFilter;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfTokenRepository.setHeaderName("X-XSRF-TOKEN");
        http.securityContext(contextConfig -> contextConfig.requireExplicitSave(false)) // ?
                .exceptionHandling(ehc -> {
                    ehc.defaultAuthenticationEntryPointFor(
                            new CustomAuthenticationEntryPoint(),
                            new AntPathRequestMatcher("/**")
                    );//브라우저 로그인 팝업 해결을 위한 코드

                    ehc.accessDeniedHandler(new CustomAccessDeniedHandler());

                })
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) //JWT 기반에서는 서버가 JWT를 기억할 필요가 없음(이론적으로)
                        .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration config = new CorsConfiguration();
                                List<String> corsDomain = Arrays.asList("https://localhost");
                                config.setAllowedOrigins(corsDomain);
                                config.setAllowedMethods(Collections.singletonList("*"));
                                config.setAllowCredentials(true);//
                                config.setAllowedHeaders(Collections.singletonList("*"));//?
                                config.setExposedHeaders(Arrays.asList("Authorization","XSRF-TOKEN", "Set-Cookie")); //헤더에 이를 넣어줘서 클라언트가 JWT을 받을 수 있게
                                config.setMaxAge(3600L); //3600밀리초
                                return config;
                            }
                        }))
                .formLogin().disable()
                .httpBasic(Customizer.withDefaults())
                .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                        .ignoringRequestMatchers("/api/register","/api/main" ,"/login/oauth2/**",  // OAuth2 콜백 경로 CSRF 무시
                                "/oauth2/**", "/", "/api/error/**", "/api/auth/**", "/api/token")
                        .csrfTokenRepository(csrfTokenRepository)) //?
                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class) //Basic 인증 필터 이전에 Basic 요청의 사전 검증
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(this.jwtGeneratorFilter, BasicAuthenticationFilter.class) //로그인 과정은 Basic Auth 이후에 토큰 생성
                .addFilterBefore(this.jwtValidatorFilter, BasicAuthenticationFilter.class)//로그인 이후에 API의 접근은 Basic Auth이전에 토큰을 먼저 검증
                //.requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // Only HTTP 프로덕션 환경시 request 루프 발생 가능
                //.requiresChannel(rcc -> rcc.anyRequest().requiresSecure()) //Only HTTPS
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/register","/api/error/**","/api/genre/**","/api/main","/api/board"
                                ,"/api/token","/login","/api/auth/**","/css/**","/js/**","/","/favicon.ico","/csrf","/actuator/**","/api/user").permitAll() //react의 초기 요청이 "/"일 수 있다?
                        .requestMatchers(HttpMethod.GET, "/api/board/content").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/board/content").authenticated()
                        .requestMatchers("/api/logout","/api/comment").authenticated()
                        .requestMatchers("/api/profile/**","/api/getMessage").hasRole("USER")
                        .requestMatchers("/api/postImage","/api/post","/api/postMessage").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/subculture").hasRole("ADMIN")
                        .anyRequest().authenticated()) //제대로 적용됨
                .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService) //kakao, naver, github와 같은 기본적 OAuth2
                        .oidcUserService(customOidcUserService) //Google 과 같은 OpenID Connect 기반 OAuth2

                )
                        .successHandler(((request, response, authentication) -> {
                            String jwt = null;
                            String refreshToken = null;
                            if(authentication.getPrincipal() instanceof  CustomUserDetails userDetails) {
                                //여기서 CustomUserDetails 객체가 아닐 수도 있다..
                                jwt = generateJwt(userDetails);
                                refreshToken = generateJwt(userDetails);
                            } else if(authentication.getPrincipal() instanceof CustomOidcUser oidcUser) {
                                String email = oidcUser.getEmail();
                                jwt = generateJwtFromOidcUser(oidcUser);
                                refreshToken = generateJwtFromOidcUser(oidcUser);

                            } else {
                                throw new IllegalArgumentException("Invalid OAuth2 Process");
                            }

/*                            Cookie jwtCookie = new Cookie("jwt",jwt);
                            jwtCookie.setHttpOnly(true);
                            jwtCookie.setSecure(true);
                            jwtCookie.setPath("/");
                            jwtCookie.setMaxAge(3600);
                            jwtCookie.setAttribute("SameSite","None");
                            response.addCookie(jwtCookie);*/
                            response.setHeader("Set-Cookie",
                                    String.format("jwt=%s; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=3600", jwt));
                            response.addHeader("Set-Cookie",
                                    String.format("refreshToken=%s; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=%d", refreshToken, 60*60*24*7));
                            response.sendRedirect("https://localhost");
                        })));

        return http.build();
    }


    @Bean
    ClientRegistrationRepository clientRegistrationRepository(){
        ClientRegistration github = githubClientRegistration();
        ClientRegistration google = googleClientRegistration();
        ClientRegistration kakao = kakaoClientRegistration();
        ClientRegistration naver = naverClientRegistration();
        return new InMemoryClientRegistrationRepository(Arrays.asList(github,google,kakao,naver));
    }

    private String generateJwt(CustomUserDetails userDetails){
        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY, ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        //HMAC-SHA 메시지의 무결성과 인증을 보장, 비밀 키를 사용해 암호화, 해시값 생성, 메시지 변조 여부 파악 가능
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(new Date());

        String jwt = Jwts.builder().issuer("SubWeb").subject("JWT") //subweb에서 발행한 JWT
                .claim("id", userDetails.getId())
                .claim("email",userDetails.getUsername())
                .claim("authorities",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","))) //권한 String으로 받아서 ,로 나누기
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + 30000000)) //밀리초
                .signWith(secretKey).compact(); //서명을 해싱된 비밀키로 하는것 그렇기에 jwt 변조여부를 쉽게 알 수 있음
        return jwt;
    }

    private String generateJwtFromOidcUser(CustomOidcUser oidcUser){
        int id  = oidcUser.getAccountId();
        String email = oidcUser.getEmail();
        Collection<? extends GrantedAuthority> authorities = oidcUser.getAuthorities();
        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY, ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
        SecretKey secretKey =Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder().issuer("SubWeb").subject("JWT") //subweb에서 발행한 JWT
                .claim("id",id)
                .claim("email",email)
                .claim("authorities",authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","))) //권한 String으로 받아서 ,로 나누기
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + 30000000)) //밀리초
                .signWith(secretKey).compact(); //서명을 해싱된 비밀키로 하는것 그렇기에 jwt 변조여부를 쉽게 알 수 있음
        return jwt;
    }
    private ClientRegistration githubClientRegistration(){
        return CommonOAuth2Provider.GITHUB.getBuilder("github")
                .clientId("***")
                .clientSecret("***")
                .redirectUri("https://localhost/login/oauth2/code/github")
                .scope("read:user","user:email")
                .build();
    }

    private ClientRegistration googleClientRegistration(){
        return CommonOAuth2Provider.GOOGLE.getBuilder("google")
                .clientId("***")
                .clientSecret("***")
                .redirectUri("https://localhost/login/oauth2/code/google") // 추가!!
                .scope("email","profile","openid")
                .build();
    } //구글은 깃허브와 다르게 email 정보를 얻기위해 따로 API를 구성하지 않아도 scope로 요청 정보만 더해주면 됨

    private ClientRegistration kakaoClientRegistration(){
        return ClientRegistration.withRegistrationId("kakao")
                .clientId("***")
                .clientSecret("***") //보통 카카오는 secret 사용안한다고 함..?
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("https://localhost/login/oauth2/code/kakao")
                .scope("profile_nickname","account_email")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .clientName("kakao")
                .build();
    }

    private ClientRegistration naverClientRegistration(){
        return ClientRegistration.withRegistrationId("naver")
                .clientId("***")
                .clientSecret("***") //보통 카카오는 secret 사용안한다고 함..?
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("https://localhost/login/oauth2/code/naver")
                .scope("name","email")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .clientName("Naver")
                .build();
    }


    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker()
    {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }//해당 사용자의 비밀번호가 유출된 적이 있는지를 검사하는 코드

}
