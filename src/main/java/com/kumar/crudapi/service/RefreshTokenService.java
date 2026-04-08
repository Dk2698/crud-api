package com.kumar.crudapi.service;

import com.kumar.crudapi.entity.AppUser;
import com.kumar.crudapi.entity.RefreshToken;
import com.kumar.crudapi.exception.TokenException;
import com.kumar.crudapi.exception.UserNotFoundException;
import com.kumar.crudapi.repository.AppUserRepository;
import com.kumar.crudapi.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final AppUserRepository appUserRepository;

    @Value("${jwt.refresh.expiry:604800000}") // 7 days in properties
    private long refreshExpiry;

    public String createToken(String username) {
        AppUser user = appUserRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshExpiry))
                .revoked(false)
                .build();

        return repo.save(token).getToken();
    }

    @Transactional
    public String rotate(String oldTokenStr) {
        RefreshToken oldToken = repo.findByToken(oldTokenStr)
                .orElseThrow(() -> new TokenException("Refresh token not found"));

// Grace Period: Allow reuse if it was revoked within the last 30 seconds
        boolean inGracePeriod = oldToken.isRevoked() &&
                oldToken.getUpdatedAt().isAfter(Instant.now().minusSeconds(30));

        if (!inGracePeriod && (oldToken.isRevoked() || oldToken.getExpiryDate().isBefore(Instant.now()))) {
            throw new TokenException("Token expired or revoked");
        }

        oldToken.setRevoked(true); // Revoke old one
        repo.save(oldToken);

        return createToken(oldToken.getUser().getUserName()); // Create new one
    }

    public RefreshToken findByToken(String token) {
        return repo.findByToken(token).orElseThrow();
    }
}