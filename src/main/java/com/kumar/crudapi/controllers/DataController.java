package com.kumar.crudapi.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DataController {

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')") // Requires ROLE_ADMIN
    public String adminAccess() { return "Admin Content"; }

    @GetMapping("/read-data")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE')") // Requires specific permission
    public String readAccess() { return "Data for Readers"; }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('WRITE_PRIVILEGE')")
    public String updateAccess() { return "Updated!"; }
}