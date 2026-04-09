package com.kumar.crudapi.controllers;

import com.kumar.crudapi.entity.Permission;
import com.kumar.crudapi.entity.Role;
import com.kumar.crudapi.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminManagementController {

    private final RoleService roleService;

    // Create a new Role: e.g., { "name": "ROLE_MODERATOR" }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    // Create a new Permission: e.g., { "name": "DELETE_USER" }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        return ResponseEntity.ok(roleService.createPermission(permission));
    }

    // --- User <-> Role ---
    @PostMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<String> assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        roleService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok("Role assigned to user");
    }

    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<String> removeRole(@PathVariable Long userId, @PathVariable Long roleId) {
        roleService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok("Role removed from user");
    }

    // --- Role <-> Permission ---
    @PostMapping("/roles/{roleId}/permissions/{permId}")
    public ResponseEntity<String> assignPermission(@PathVariable Long roleId, @PathVariable Long permId) {
        roleService.assignPermissionToRole(roleId, permId);
        return ResponseEntity.ok("Permission assigned to role");
    }

    @DeleteMapping("/roles/{roleId}/permissions/{permId}")
    public ResponseEntity<String> removePermission(@PathVariable Long roleId, @PathVariable Long permId) {
        roleService.removePermissionFromRole(roleId, permId);
        return ResponseEntity.ok("Permission removed from role");
    }
}