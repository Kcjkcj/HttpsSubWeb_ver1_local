package com.kcj.SubWebOAuth2WithAWS.controller;

import com.kcj.SubWebOAuth2WithAWS.entity.Account;
import com.kcj.SubWebOAuth2WithAWS.entity.Role;
import com.kcj.SubWebOAuth2WithAWS.repository.AccountRepository;
import com.kcj.SubWebOAuth2WithAWS.repository.RoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @PostMapping("/api/register")
    public ResponseEntity<String> registerUser(@RequestBody Account account){
        try{
            String hashPwd = passwordEncoder.encode(account.getAccountPwd());
            account.setAccountPwd(hashPwd);
            account.setCreateDt(new Date(System.currentTimeMillis()));
            Account savedAccount = accountRepository.save(account);
            Role role = new Role();
            role.setRole_name("ROLE_USER");
            role.setAccount(savedAccount);
            Role savedRole = roleRepository.save(role);

            if(savedAccount.getAccountId()>0 && savedRole.getRole_id()>0)
            {
                return ResponseEntity.status(HttpStatus.CREATED).
                        body("성공적으로 등록되었습니다.");
            }
            else
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                        body("등록에 실패하였습니다.");
            }
        }
        catch (Exception ex)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body("예외가 발생하였습니다 : "+ex.getMessage());
        }
    }


    @RequestMapping("/api/user")
    public Account getAccountDetails(Authentication authentication)
    {
        Optional<Account> optionalAccount = accountRepository.findByEmail(authentication.getName());
        return optionalAccount.orElse(null);
    }



    @PostMapping("/api/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response){
        ResponseCookie jwtCookie = ResponseCookie.from("jwt","")
                .path("/")
                .maxAge(0)//즉시 만료
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie",jwtCookie.toString());
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken","")
                .path("/")
                .maxAge(0)//즉시 만료
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie",refreshTokenCookie.toString());

        ResponseCookie csrfTokenCookie = ResponseCookie.from("XSRF-TOKEN", "")
                .path("/")
                .maxAge(0)
                .httpOnly(false) // 원래 false로 발급됨
                .secure(true)
                .sameSite("Lax") // 발급 시와 동일하게
                .build();
        response.addHeader("Set-Cookie", csrfTokenCookie.toString());

        ResponseCookie jsessionCookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax") // JSESSIONID도 Lax일 가능성 높음
                .build();
        response.addHeader("Set-Cookie", jsessionCookie.toString());

        return ResponseEntity.ok().build();
    }
}
