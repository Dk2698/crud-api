package com.kumar.crudapi.controllers;

import com.kumar.crudapi.entity.AppUser;
import com.kumar.crudapi.entity.RefreshToken;
import com.kumar.crudapi.security.JwtUtil;
import com.kumar.crudapi.service.AuthService;
import com.kumar.crudapi.service.RefreshTokenService;
import com.kumar.crudapi.service.dto.AuthRequest;
import com.kumar.crudapi.service.dto.AuthResponse;
import com.kumar.crudapi.service.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {
        // 1. Authenticate credentials
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUserName(), req.getPassword())
        );

        // 2. Delegate token creation to Service
        return authService.login(req.getUserName());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String oldToken = request.get("refresh_token");

        // 1. Rotate the token (Revokes old, creates new)
        String newRefresh = refreshTokenService.rotate(oldToken);

        // 2. Fetch the RefreshToken entity to get the associated User
        RefreshToken rt = refreshTokenService.findByToken(newRefresh);

        // 3. Get the full AppUser object
        AppUser user = rt.getUser();

        // 4. Pass the USER OBJECT (not just name) to include Roles/Permissions in JWT
        String newAccess = jwtUtil.generateAccessToken(user);

        return ResponseEntity.ok(new AuthResponse(newAccess, newRefresh));
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String token) {
        authService.blacklist(token.substring(7));
        return "Logged out";
    }
}
