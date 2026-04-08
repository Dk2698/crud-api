package com.kumar.crudapi.service;

import com.kumar.crudapi.entity.AppUser;
import com.kumar.crudapi.repository.AppUserRepository;
import com.kumar.crudapi.service.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor // Cleaner than @Autowired on fields
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder encoder;

    public void register(RegisterRequest req) {
        // Validation check for existing users
        if (appUserRepository.findByUserName(req.getUserName()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        AppUser user = new AppUser();
        user.setUserName(req.getUserName());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setEnabled(true);
        // Soft delete flag 'deleted' defaults to false

        appUserRepository.save(user);
    }

    // This is now handled by RefreshTokenService in your controller
    // but kept here if you want a simple UUID fallback.
    public String createRefreshToken(String username) {
        return UUID.randomUUID().toString();
    }

    public void blacklist(String token) {
//        redisTemplate.opsForValue().set(token, "blacklisted");
    }
}