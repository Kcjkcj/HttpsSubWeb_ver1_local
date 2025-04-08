package com.kcj.SubWebOAuth2WithAWS.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    public boolean hasAccessToResource(int resourceOwnerId, Authentication authentication)
    {
        if(authentication.getPrincipal() instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId() == resourceOwnerId || hasAdminRole(authentication);
        }
        else
            return false;
    }

    public boolean hasAdminRole(Authentication authentication)
    {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
