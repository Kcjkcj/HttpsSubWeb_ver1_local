package com.kcj.SubWebOAuth2WithAWS.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubwebAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if(passwordEncoder.matches(pwd,userDetails.getPassword())) //해당 기능을 default에서는 일부러꺼서 테스트를 더 쉽게하는 방법도 있음
            return new UsernamePasswordAuthenticationToken(userDetails,pwd,userDetails.getAuthorities());
        //단순 username을 넣어주는게 아니라 Details를 넣어줘야 CustomUserDetails 에서 작성한 Id값을 활용할 수 있음
        else
            throw new BadCredentialsException("비밀번호가 틀렸습니다.");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
    //2개의 멤버 함수 모두가 있어야 함
}
