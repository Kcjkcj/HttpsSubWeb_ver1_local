package com.kcj.SubWebOAuth2WithAWS.config;

//Controller (@RestController): 클라이언트 요청을 받고 응답을 보냄.
//Service (@Service): 비즈니스 로직을 처리함.
//Repository (@Repository): DB와 직접적으로 데이터를 주고받음.

import com.kcj.SubWebOAuth2WithAWS.entity.Account;
import com.kcj.SubWebOAuth2WithAWS.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service //비즈니스 로직 처리 (로그인,
@RequiredArgsConstructor
public class SubwebUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { //loadUserByUsername은 String만 받음
        Account account  = accountRepository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException("해당 id 유저 찾지 못함 :" + email));

        //List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(account.getAccountRole())); //현재는 유저당 하나의 권한만 줄 수 있음
        List<GrantedAuthority> authorities = account.getRoles().stream().
                map(role -> new SimpleGrantedAuthority(role.getRole_name())).collect(Collectors.toList()); //여러개의 role을 하나의 유저에게 줄 수 있게
        return new CustomUserDetails(account.getAccountId(), account.getEmail(), account.getAccountPwd(), authorities);
        //여기서 User를 return하는게 아니라 CustomUserDetails를 리턴해서 Id값까지 넘겨줘야 토큰 발행에 사용할 수 있음
    }

}
