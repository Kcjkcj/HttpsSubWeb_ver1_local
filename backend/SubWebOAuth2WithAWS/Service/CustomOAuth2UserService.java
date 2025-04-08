package com.kcj.SubWebOAuth2WithAWS.Service;

import com.kcj.SubWebOAuth2WithAWS.config.CustomUserDetails;
import com.kcj.SubWebOAuth2WithAWS.entity.Account;
import com.kcj.SubWebOAuth2WithAWS.entity.Role;
import com.kcj.SubWebOAuth2WithAWS.oauth2.OAuth2UserInfo;
import com.kcj.SubWebOAuth2WithAWS.oauth2.OAuth2UserInfoFactory;
import com.kcj.SubWebOAuth2WithAWS.repository.AccountRepository;
import com.kcj.SubWebOAuth2WithAWS.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                userRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes(),
                accessToken
        );

        Account account = accountRepository.findByEmail(userInfo.getEmail()).orElseGet(() -> {
            Account newAccount = new Account();
            String email = userInfo.getEmail();
            if(email == null){
                throw new RuntimeException("OAuth2 로그인 실패: 이메일이 없습니다. attributes = " + attributes);
            }
            newAccount.setEmail(email);
            newAccount.setAccountName(email);
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

        List<GrantedAuthority> authorities = account.getRoles().stream().map
                (role->new SimpleGrantedAuthority(role.getRole_name())).collect(Collectors.toList());


        CustomUserDetails userDetails = new CustomUserDetails(
                account.getAccountId(),
                account.getEmail(),
                account.getAccountPwd(),
                authorities
        );

        userDetails.setAttributes(oAuth2User.getAttributes());
        return userDetails;
    }
}
