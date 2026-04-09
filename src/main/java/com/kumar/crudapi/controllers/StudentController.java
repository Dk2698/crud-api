package com.kumar.crudapi.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @PostMapping
    @PreAuthorize("@ss.hasPermission('ROLE', 'USER')")
    public String create() { return "Created"; }

    @GetMapping
    @PreAuthorize("@ss.hasPermission('JANUS', 'READ')")
    public String read() { return "Read"; }

    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermission('STUDENT', 'EXPORT')")
    public String export() { return "Exported"; }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('STUDENT', 'DELETE')")
    public String delete(@PathVariable Long id) {
        return "Deleted";
    }

//    	@PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('STUDENT_READ')")
//    	@PreAuthorize("hasRole('ADMIN') or hasAuthority('STUDENT_READ')")
}