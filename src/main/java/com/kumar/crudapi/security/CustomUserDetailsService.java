package com.kumar.crudapi.security;

import com.kumar.crudapi.entity.AppUser;
import com.kumar.crudapi.repository.AppUserRepository;
import com.kumar.crudapi.repository.UserRepository;
import com.kumar.crudapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = appUserRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
//                .roles(user.getRoles().stream()
//                        .map(r -> r.getName().replace("ROLE_", ""))
//                        .toArray(String[]::new))
                .authorities(
                        user.getRoles().stream()
                                .flatMap(role -> role.getPermissions().stream())
                                .map(p -> new SimpleGrantedAuthority(p.getName()))
                                .toList()
                )
                .build();
    }
}
