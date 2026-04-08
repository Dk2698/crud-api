package com.kumar.crudapi.service;

import com.kumar.crudapi.entity.AppUser;
import com.kumar.crudapi.entity.Permission;
import com.kumar.crudapi.entity.Role;
import com.kumar.crudapi.repository.AppUserRepository;
import com.kumar.crudapi.repository.PermissionRepository;
import com.kumar.crudapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public Permission createPermission(Permission permission) {
        // Optional: Check if it already exists
        if (permissionRepository.findByName(permission.getName()).isPresent()) {
            throw new RuntimeException("Permission already exists");
        }
        return permissionRepository.save(permission);
    }

    @Transactional
    public Role createRole(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new RuntimeException("Role already exists");
        }
        // Initialize an empty set if not provided in JSON
        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }
        return roleRepository.save(role);
    }

    @Transactional
    public void assignRoleToUser(Long userId, Long roleId) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();
        user.getRoles().add(role);
        // userRepository.save(user); // Optional if using @Transactional
    }

    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();
        user.getRoles().remove(role);
    }

    @Transactional
    public void assignPermissionToRole(Long roleId, Long permId) {
        Role role = roleRepository.findById(roleId).orElseThrow();
        Permission permission = permissionRepository.findById(permId).orElseThrow();
        role.getPermissions().add(permission);
    }

    @Transactional
    public void removePermissionFromRole(Long roleId, Long permId) {
        Role role = roleRepository.findById(roleId).orElseThrow();
        Permission permission = permissionRepository.findById(permId).orElseThrow();
        role.getPermissions().remove(permission);
    }
}