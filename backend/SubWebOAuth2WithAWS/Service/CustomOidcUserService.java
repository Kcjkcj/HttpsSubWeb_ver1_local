package com.kcj.SubWebOAuth2WithAWS.Service;


import com.kcj.SubWebOAuth2WithAWS.config.CustomOidcUser;
import com.kcj.SubWebOAuth2WithAWS.entity.Account;
import com.kcj.SubWebOAuth2WithAWS.entity.Role;
import com.kcj.SubWebOAuth2WithAWS.repository.AccountRepository;
import com.kcj.SubWebOAuth2WithAWS.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String,Object> attributes = oidcUser.getAttributes();
        String email = (String)attributes.get("email");
        String name = (String)attributes.get("name");

        if(email==null){
            throw new RuntimeException("OAuth2 로그인 실패 : 없는 email");
        }

        Account account = accountRepository.findByEmail(email).orElseGet(() -> {
            Account newAccount = new Account();
            newAccount.setEmail(email);
            newAccount.setAccountName(name);
            newAccount.setCreateDt(new Date(System.currentTimeMillis()));
            newAccount.setAccountPwd(passwordEncoder.encode("OAUTH2_DUMMY_PASSWORD"));
            Account savedaccount = accountRepository.save(newAccount);
            Role role = new Role();
            role.setRole_name("ROLE_USER");
            role.setAccount(savedaccount);
            savedaccount.getRoles().add(role); //Acount -> Role 연결
            roleRepository.save(role);
            return savedaccount;
        });


        // Spring Security 권한 매핑
        List<GrantedAuthority> authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole_name()))
                .collect(Collectors.toList());

        return new CustomOidcUser(authorities,oidcUser.getIdToken(),account.getAccountId());

    }
}
