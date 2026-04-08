package com.kumar.crudapi.service;

import com.kumar.crudapi.entity.AppUser;
import com.kumar.crudapi.entity.Permission;
import com.kumar.crudapi.entity.Role;
import com.kumar.crudapi.repository.AppUserRepository;
import com.kumar.crudapi.repository.PermissionRepository;
import com.kumar.crudapi.repository.RoleRepository;
import com.kumar.crudapi.service.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder encoder;

    @Transactional
    public void register(RegisterRequest req) {
        // 1. Check if user exists
        if (appUserRepository.findByUserName(req.getUserName()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // 2. Determine Role Name
        String roleName = req.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";

        // 3. Get or Create Role dynamically
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> createDefaultRole(roleName));

        // 4. Create User
        AppUser user = new AppUser();
        user.setUserName(req.getUserName());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Set.of(role)));

        appUserRepository.save(user);
    }

    private Role createDefaultRole(String roleName) {
        // Ensure the default permission exists too
        Permission readPerm = createDefaultPermission();

        Role newRole = new Role();
        newRole.setName(roleName);
        newRole.setPermissions(new HashSet<>(Set.of(readPerm)));
        return roleRepository.save(newRole);
    }

    private @NonNull Permission createDefaultPermission() {
        return permissionRepository.findByName("READ_PRIVILEGE")
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setName("READ_PRIVILEGE");
                    return permissionRepository.save(p);
                });
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