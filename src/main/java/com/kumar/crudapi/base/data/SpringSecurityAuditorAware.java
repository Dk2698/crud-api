package com.kumar.crudapi.base.data;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth == null || !auth.isAuthenticated()) {
//            return Optional.of("SYSTEM");
//        }
//
//        return Optional.of(auth.getName());

        return Optional.of("system"); // fallback for testing/H2
    }
}
