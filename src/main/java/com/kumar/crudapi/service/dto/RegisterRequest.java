package com.kumar.crudapi.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private boolean isAdmin;
}
