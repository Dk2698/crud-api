package com.kumar.crudapi.controllers;

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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUserName(), req.getPassword())
        );

        String accessToken = jwtUtil.generateAccessToken(req.getUserName());
        String refreshToken = refreshTokenService.createToken(req.getUserName());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String,String> request) {
        String oldToken = request.get("refresh_token");
        // verify() and rotate() handle the business logic
        String newRefresh = refreshTokenService.rotate(oldToken);

        // Extract user from the newly rotated token context
        RefreshToken rt = refreshTokenService.findByToken(newRefresh);
        String newAccess = jwtUtil.generateAccessToken(rt.getUser().getUserName());

        return ResponseEntity.ok(new AuthResponse(newAccess, newRefresh));
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String token) {
        authService.blacklist(token.substring(7));
        return "Logged out";
    }
}
