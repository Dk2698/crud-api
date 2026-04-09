package com.kumar.crudapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component("ss")
public class SecurityService {

    public boolean hasPermission(String resource, String action) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        String requiredPermission = resource.toUpperCase() + "_" + action.toUpperCase();
        String wildcardPermission = resource.toUpperCase() + "_*";

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals(requiredPermission) ||
                        a.equals(wildcardPermission) ||
                        a.equals("ROLE_ADMIN") ||      // Full System Admin
                        a.equals("SUPER_*"));          // Global Wildcard
    }
}
