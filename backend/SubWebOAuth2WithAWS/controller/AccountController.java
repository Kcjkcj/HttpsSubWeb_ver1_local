package com.kcj.SubWebOAuth2WithAWS.controller;

import com.kcj.SubWebOAuth2WithAWS.config.CustomUserDetails;
import com.kcj.SubWebOAuth2WithAWS.config.SecurityService;
import com.kcj.SubWebOAuth2WithAWS.entity.Account;
import com.kcj.SubWebOAuth2WithAWS.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController //Spring MVC에서 RESTful 웹 서비스를 쉽게 만들 수 있게 해주는 특별한 컨트롤러
@RequiredArgsConstructor //생성자 주입을 자동화
//@Autowired
public class AccountController { //내 계정 정보를 볼 때 사용함
    private final AccountRepository accountRepository;
    private final SecurityService securityService;
    @GetMapping("/api/profile")
    public Account GetAccountInfo(@AuthenticationPrincipal UserDetails userDetails, Authentication authentication) //유저 id로 찾아야지
    { //왜 Account에서 ResponseEntity로 바뀌어야 하는가?
        int id = ((CustomUserDetails)userDetails).getId();
        if(securityService.hasAccessToResource(id,authentication)) {
            Optional<Account> myAccount = accountRepository.findByAccountId(id);
            return myAccount.orElse(null);
        }
        else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN); //403
    }

    @PostMapping(value = "/api/profile/search")
    public ResponseEntity<?> SearchUser(Authentication authentication,
                                        @RequestParam("nickname")String nickname) //유저 id로 찾아야지
    { //왜 Account에서 ResponseEntity로 바뀌어야 하는가?
        String keyword = "%"+nickname+"%";
            List<Account> accounts = accountRepository.findByAccountNameLike(keyword);
            if(accounts.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).
                        body("해당하는 사용자가 없습니다.");

            List<Map<String, Object>> recommendation = accounts.stream()
                    .map(account -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("accountId",account.getAccountId());
                        map.put("nickname",account.getAccountName());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(recommendation);
    }

}
